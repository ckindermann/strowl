package uk.ac.man.cs.exp;

import uk.ac.man.cs.util.*;
import uk.ac.man.cs.ont.*;
import uk.ac.man.cs.pat.*;

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

public class MultipleReuse {

    public static void main(String[] args) throws Exception {
        String ontFilePath = args[0];//ontology to be tested
        String classificationFilePath = args[1];//materialsied classification of ontology
        String output = args[2];//materialsied classification of ontology

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();

        OWLOntology ontology = OntologyLoader.load(ontFilePath);
        OWLOntology classification = OntologyLoader.load(classificationFilePath);

        ReuseMiner miner = new ReuseMiner(ontology, classification);
        Map<OWLClass,CLODPReuse> reuse = miner.getDirectReuse();

        //multiple reuse
        int multiple = 0;
        Set<OWLClass> involvedInMultipleReuse = new HashSet<>();
        Map<OWLClass,Set<OWLClass>> class2subclass = miner.getClass2SubClass();
        //all classes with siubclases
        for(Map.Entry<OWLClass, Set<OWLClass>> entry : class2subclass.entrySet()) {
            OWLClass key = entry.getKey();
            Set<OWLClass> value = entry.getValue(); 

            //check whether key reuses CLODP
            if(!reuse.containsKey(key))
                continue;

            //check whether its subclasses do as well
            int count = 0;
            for(OWLClass c : value){
                if(reuse.containsKey(c)){
                    if(!reuse.get(c).isEmpty()){
                        involvedInMultipleReuse.add(c);
                        count++;
                    }
                }
            }
            if(count > 1){
                multiple++;
            }
        } 
        System.out.println("Multiple " + multiple);
        System.out.println("Classes involved in multiple " + involvedInMultipleReuse.size());

        OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory(); 
        OWLClass bottom = factory.getOWLNothing(); 

        //how many non-leaf classes?
        Set<OWLClass> nonLeaf = new HashSet<>();
        for(Map.Entry<OWLClass, Set<OWLClass>> entry : class2subclass.entrySet()) {
            OWLClass key = entry.getKey();
            Set<OWLClass> value = entry.getValue(); 
            value.remove(bottom);
            //remove bottom
            if(!value.isEmpty())
                nonLeaf.add(key);
        }
        System.out.println("Non-leafs " + nonLeaf.size());
        int nonleafReuse = 0;
        for(OWLClass nl : nonLeaf){
            if(reuse.containsKey(nl)){
                nonleafReuse++;
            }
        }
        System.out.println("Non-leafs reuse " + nonleafReuse);
    }
}
