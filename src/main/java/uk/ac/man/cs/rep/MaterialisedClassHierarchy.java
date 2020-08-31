package uk.ac.man.cs.rep;

import uk.ac.man.cs.util.*;
import uk.ac.man.cs.ont.*;

import java.util.*;
import java.util.stream.*;
import java.io.File;
import java.nio.file.*;

import org.semanticweb.owlapi.model.*; 
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.model.parameters.Imports;


/**
 * Created by chris on 19/08/20.
 */

public class MaterialisedClassHierarchy {

    public static void main(String[] args) throws  Exception {

        String ontFilePath = args[0];//ontology to be tested
        String outputPath = args[1];

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();
        OWLOntology ont = OntologyLoader.load(ontFilePath);
        MaterialisedClassHierarchy mch = new MaterialisedClassHierarchy(ont);

        Map<OWLClass,Set<OWLClass>> class2superclass = mch.getClass2superclass();
        Map<OWLClass,Set<OWLAxiom>> class2hierarchyFrame = mch.getClass2HierarchyFrame();
        Set<OWLClass> leafs = mch.getLeafClasses();

        int stop = 10;
        int count = 1;
        for(OWLClass l : leafs){
            if(count > stop)
                break;
            System.out.println("Leaf Class " + l);
            Set<OWLAxiom> frame = class2hierarchyFrame.get(l);
            if(frame.size() > 5){
                for(OWLAxiom a : frame){
                    System.out.println("\t" + a); 
                } 
                count++;
            }
        } 
    }

    private OWLOntology ontology;

    private Map<OWLClass,Set<OWLClass>> class2superclass;//includes equivs (and the class itself)
    private Map<OWLClass,Set<OWLAxiom>> class2hierarchyFrame;//subclassOf + equiv
    private Set<OWLClass> leafClasses;//there is no (told) subclass of this class 

    public MaterialisedClassHierarchy(OWLOntology o){ 
        this.ontology = o;
        this.initialise(); 
    }

    public Map<OWLClass,Set<OWLClass>> getClass2superclass(){
        return this.class2superclass;
    }

    public Map<OWLClass,Set<OWLAxiom>> getClass2HierarchyFrame(){
        return this.class2hierarchyFrame;
    }

    public Set<OWLClass> getLeafClasses(){
        return this.leafClasses;
    }

    private void initialise(){
        //initialise maps
        this.class2superclass = new HashMap<>();
        this.class2hierarchyFrame = new HashMap<>();
        Set<OWLAxiom> axioms = this.ontology.getAxioms(Imports.INCLUDED); 
        for(OWLAxiom a : axioms){
            if(a instanceof OWLSubClassOfAxiom){
                this.handleSubClassOfAxiom((OWLSubClassOfAxiom) a);
            }
            if(a instanceof OWLEquivalentClassesAxiom){
                this.handleEquivalenceAxiom((OWLEquivalentClassesAxiom) a);
            }
        }

        //initialise leaf classes
        this.initialiseLeafClasses();
    }


    private void initialiseLeafClasses(){
        this.leafClasses =  this.ontology.getClassesInSignature(Imports.INCLUDED);
        System.out.println("Number of classes  " + this.leafClasses.size());
        //for all classes, test whether there are no children
        for (Map.Entry<OWLClass, Set<OWLClass>> entry : this.class2superclass.entrySet()) {
            OWLClass subclass = entry.getKey();
            Set<OWLClass> superclasses = entry.getValue();
            superclasses.remove(subclass); 
            leafClasses.removeAll(superclasses);
        } 
        System.out.println("Number of leaf classes  " + this.leafClasses.size());
    }


    private void handleSubClassOfAxiom(OWLSubClassOfAxiom a){
        OWLClassExpression subclass = a.getSubClass();
        OWLClassExpression superclass =  a.getSuperClass();

        if(!subclass.isAnonymous()){
            //add to "(direct) frame view"
            this.class2hierarchyFrame.putIfAbsent(subclass.asOWLClass(),new HashSet<>());
            this.class2hierarchyFrame.get(subclass).add(a);
        }

        if(isAtomicSubsumption(a)){ 
            //add to "inheritance list"
            this.class2superclass.putIfAbsent(subclass.asOWLClass(),new HashSet<>());
            this.class2superclass.get(subclass).add(superclass.asOWLClass());
            this.class2superclass.get(subclass).add(subclass.asOWLClass());
        }
    }

    private void handleEquivalenceAxiom(OWLEquivalentClassesAxiom a){
        Set<OWLClass> namedClasses = a.getNamedClasses();
        for(OWLClass c : namedClasses){

            //add to "(direct) frame view"
            this.class2hierarchyFrame.putIfAbsent(c,new HashSet<>());
            this.class2hierarchyFrame.get(c).add(a);

            //add to "inheritance list"
            this.class2superclass.putIfAbsent(c,new HashSet<>());
            this.class2superclass.get(c).addAll(namedClasses); 
        }
    }


    public Set<OWLClass> getTransitiveSuperclasses(OWLClass c){
        Set<OWLClass> superclasses = new HashSet<>();

        return superclasses; 
    }

    private boolean isAtomicSubsumption(OWLAxiom a){
        if(a instanceof OWLSubClassOfAxiom){
            OWLClassExpression subclass = ((OWLSubClassOfAxiom) a).getSubClass();
            OWLClassExpression superclass = ((OWLSubClassOfAxiom) a).getSuperClass();
            if(!subclass.isAnonymous() && !superclass.isAnonymous())
                return true; 
        }
        return false;
    } 
}
