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

public class TestCarcinoma {

    public static void main(String[] args) throws Exception {
        String ontFilePath = args[0];//ontology to be tested
        String classificationFilePath = args[1];//materialsied classification of ontology
        String output = args[2];//materialsied classification of ontology

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();

        OWLOntology ontology = OntologyLoader.load(ontFilePath);
        OWLOntology classification = OntologyLoader.load(classificationFilePath);

        try {
            OWLReasoner reasoner = ReasonerLoader.initReasoner(ReasonerName.get("HERMIT"),classification); 
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY); 

            for(OWLClass c : classification.getClassesInSignature()){
                //find
                //DOID_3702
                //cervical_carcinoma

                if(c.getIRI().toString().contains("DOID_3702") ||
                   c.getIRI().toString().contains("cervical_carcinoma")){
                    System.out.println("----");
                    System.out.println(c.getIRI().toString());
                    Set<OWLClass> superclasses = reasoner.getSuperClasses(c,false).getFlattened();
                    for(OWLClass s : superclasses){
                        System.out.println(s.getIRI().toString());
                    }
                    System.out.println("----");
                   }
            }

        } catch (Exception e){
            System.out.println("ERROR: Could not classify ontology");
        }

    }
}
