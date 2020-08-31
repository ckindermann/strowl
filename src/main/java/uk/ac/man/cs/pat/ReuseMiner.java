package uk.ac.man.cs.pat;

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
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.InferenceType;


/**
 * Created by chris on 20/08/20.
 */

public class ReuseMiner {

    public static void main(String[] args) throws Exception {
        String ontFilePath = args[0];//ontology to be tested
        String classificationFilePath = args[1];//materialsied classification of ontology
        String output = args[2];//materialsied classification of ontology

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();

        OWLOntology ontology = OntologyLoader.load(ontFilePath);
        OWLOntology classification = OntologyLoader.load(classificationFilePath);

        MyTimer timer = new MyTimer();
        timer.go();
        ReuseMiner miner = new ReuseMiner(ontology, classification);
        timer.stop(ontologyName + " classification"); 
    }

    private OWLOntology ontology;
    private OWLOntology classification;

    private Map<OWLClass,Set<OWLClass>> class2superclass;//according to inferred class hierarchy
    private Map<OWLClass,Set<OWLClass>> class2subclasses;//according to inferred class hierarchy
    private Map<OWLClass,Set<OWLAxiom>> class2frame;//subclassOf + equiv

    private Map<OWLClass,CLODPReuse> class2directReuse;
    private Map<OWLClass,CLODPReuse> class2indirectReuse;
    private Map<OWLClass,CLODPReuse> class2reuse;

    //private Set<OWLClass> leafClasses;//classes with no children

    private Set<String> patternSignature;

    public ReuseMiner(OWLOntology o, OWLOntology c){
        this.ontology = o;
        this.classification = c;
        this.initialiseSuperclasses();
        this.initialiseFrames();
        this.initialiseDirectReuse();
        this.initialiseIndirectReuse();
        this.initialiseReuse();
    }

    public Map<OWLClass,Set<OWLClass>> getClass2SubClass(){
        return this.class2subclasses;
    }

    public Map<OWLClass,CLODPReuse> getDirectReuse(){
        return this.class2directReuse;
    }

    public Map<OWLClass,CLODPReuse> getIndirectReuse(){
        return this.class2indirectReuse;
    }

    public Map<OWLClass,CLODPReuse> getReuse(){
        return this.class2reuse;
    }

    private void initialiseReuse(){
        this.class2reuse = new HashMap<>();
        for(OWLClass c : this.class2superclass.keySet()){
            Set<OWLClass> superclasses = this.class2superclass.get(c);
            Set<OWLAxiom> inheritance = new HashSet<>();
            if(this.class2frame.containsKey(c)){
                inheritance.addAll(this.class2frame.get(c));//direct reuse
            }
            for(OWLClass s : superclasses){
                if(this.class2frame.containsKey(s)){ 
                    inheritance.addAll(this.class2frame.get(s));//indirect reuse
                }
            }
            CLODPReuse reuse = new CLODPReuse(c,inheritance);
            if(!reuse.isEmpty()){
                this.class2reuse.put(c,reuse);
            } 
        }
    }

    private void initialiseDirectReuse(){
        this.class2directReuse = new HashMap<>();
        for (Map.Entry<OWLClass, Set<OWLAxiom>> entry : this.class2frame.entrySet()) {
            OWLClass key = entry.getKey();
            Set<OWLAxiom> value = entry.getValue();
            CLODPReuse reuse = new CLODPReuse(key);
            reuse.setReuse(value);
            if(!reuse.isEmpty()){
                this.class2directReuse.put(key,reuse);
            }
        }
    }

    private void initialiseIndirectReuse(){
        this.class2indirectReuse = new HashMap<>();
        //go through all classes and get all their superclasses
        for(OWLClass c : this.class2superclass.keySet()){
            Set<OWLClass> superclasses = this.class2superclass.get(c);
            Set<OWLAxiom> inheritance = new HashSet<>();
            for(OWLClass s : superclasses){
                if(this.class2frame.containsKey(s)){
                    inheritance.addAll(this.class2frame.get(s));
                }
            }
            CLODPReuse indirectReuse = new CLODPReuse(c,inheritance);
            if(!indirectReuse.isEmpty()){
                this.class2indirectReuse.put(c,indirectReuse);
            } 
        } 
    }

    private void initialiseFrames(){
        this.class2frame = new HashMap<>();
        for(OWLSubClassOfAxiom axiom : this.ontology.getAxioms(AxiomType.SUBCLASS_OF, Imports.INCLUDED)){
            OWLClassExpression lhs = axiom.getSubClass(); 
            if(!lhs.isAnonymous()){
                this.class2frame.putIfAbsent(lhs.asOWLClass(), new HashSet<>());
                this.class2frame.get(lhs).add(axiom);
            }
        }

        //for(OWLEquivalentClassesAxiom axiom : this.ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES, Imports.INCLUDED)){
        //    Set<OWLClassExpression> equivs = axiom.getClassExpressions();
        //    for(OWLClassExpression exp : equivs){
        //        if(!exp.isAnonymous()){
        //            this.class2frame.putIfAbsent(exp.asOWLClass(), new HashSet<>());
        //            this.class2frame.get(exp).add(axiom);
        //        }
        //    }
        //}
    }

    private void initialiseSuperclasses(){ 
        try {
            OWLReasoner reasoner = ReasonerLoader.initReasoner(ReasonerName.get("HERMIT"),this.classification); 
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY); 

            this.class2superclass = new HashMap<>();
            this.class2subclasses = new HashMap<>();
            for(OWLClass c : this.classification.getClassesInSignature()){
                Set<OWLClass> superclasses = reasoner.getSuperClasses(c,true).getFlattened();
                this.class2superclass.put(c,superclasses); 
                Set<OWLClass> subclasses = reasoner.getSubClasses(c,true).getFlattened();
                this.class2subclasses.put(c,subclasses);
            }

        } catch (Exception e){
            System.out.println("ERROR: Could not classify ontology");
        }
    }
}

