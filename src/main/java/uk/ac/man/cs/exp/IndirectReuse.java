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

public class IndirectReuse {

    public static void main(String[] args) throws Exception {
        String ontFilePath = args[0];//ontology to be tested
        String classificationFilePath = args[1];//materialsied classification of ontology
        String output = args[2];//materialsied classification of ontology

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();

        OWLOntology ontology = OntologyLoader.load(ontFilePath);
        OWLOntology classification = OntologyLoader.load(classificationFilePath);

        ReuseMiner miner = new ReuseMiner(ontology, classification);
        Map<OWLClass,CLODPReuse> reuse = miner.getIndirectReuse();
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int more = 0;

        for(CLODPReuse r : reuse.values()){
            int size = r.size();
            if(size == 1)
                one++;
            if(size == 2)
                two++;
            if(size == 3)
                three++;
            if(size == 4)
                four++;
            if(size > 4)
                more++; 
        }


        //indirect reuse sizes
        System.out.println("Indirect Reuse " + reuse.size());
        System.out.println("One axiom reuse " + one);
        System.out.println("Two axiom reuse " + two);
        System.out.println("Three axiom reuse " + three);
        System.out.println("Four axiom reuse " + four);
        System.out.println("more axiom reuse " + more);
    }
}
