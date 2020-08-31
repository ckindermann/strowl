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

public class AxiomRepetition {
    public static void main(String[] args) throws Exception {
        String ontFilePath = args[0];//ontology to be tested
        String classificationFilePath = args[1];//materialsied classification of ontology
        String output = args[2];//materialsied classification of ontology

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();

        OWLOntology ontology = OntologyLoader.load(ontFilePath);
        OWLOntology classification = OntologyLoader.load(classificationFilePath);

        ReuseMiner miner = new ReuseMiner(ontology, classification);

        Map<OWLClass,CLODPReuse> direct = miner.getDirectReuse();
        Map<OWLClass,CLODPReuse> indirect = miner.getReuse();

        int axiomRepDirect = 0;
        int diseasModelDirect = 0;
        int specifiedInputDirect = 0;
        int derivesFromPatientDirect = 0;
        int derivesFromDirect = 0;
        int cellLinerepositoryDirect = 0;

        for(CLODPReuse r : direct.values()){
            if(r.hasRepetition()){
                axiomRepDirect++;
            }
            if(r.hasDiseaseModelRepetition()){
                diseasModelDirect++;
            }
            if(r.hasSpecifiedInputRepetition()){
                specifiedInputDirect++;
            }
            if(r.hasDerivesFromPatientRepetition()){
                derivesFromPatientDirect++;
            }
            if(r.hasDerivesFromRepetition()){
                derivesFromDirect++;
            }
            if(r.hasCellLineRepistoryRepetition()){
                cellLinerepositoryDirect++;
            }
        }

        int axiomRepIndirect = 0;
        int diseasModelIndirect = 0;
        int specifiedInputIndirect = 0;
        int derivesFromPatientIndirect = 0;
        int derivesFromIndirect = 0;
        int cellLinerepositoryIndirect = 0;
        for(CLODPReuse r : indirect.values()){
            if(r.hasRepetition()){
                axiomRepIndirect++;
            } 
            if(r.hasDiseaseModelRepetition()){
                diseasModelIndirect++;
            }
            if(r.hasSpecifiedInputRepetition()){
                specifiedInputIndirect++;
            }
            if(r.hasDerivesFromPatientRepetition()){
                derivesFromPatientIndirect++;
            }
            if(r.hasDerivesFromRepetition()){
                derivesFromIndirect++;
            }
            if(r.hasCellLineRepistoryRepetition()){
                cellLinerepositoryIndirect++;
            }
        }

        System.out.println("Direct " + axiomRepDirect);
        System.out.println("Direct DiseaseModel " + diseasModelDirect);
        System.out.println("Direct SpecifiedInput " + specifiedInputDirect);
        System.out.println("Direct DerivesFromPatient " + derivesFromPatientDirect);
        System.out.println("Direct DerivesFrom " + derivesFromDirect);
        System.out.println("Direct CellLineRepositoru " + cellLinerepositoryDirect);

        System.out.println("Indirect " + axiomRepIndirect);
        System.out.println("Indirect DiseaseModel " + diseasModelIndirect);
        System.out.println("Indirect SpecifiedInput " + specifiedInputIndirect);
        System.out.println("Indirect DerivesFromPatient " + derivesFromPatientIndirect);
        System.out.println("Indirect DerivesFrom " + derivesFromIndirect);
        System.out.println("Indirect CellLineRepositoru " + cellLinerepositoryIndirect);

    }

}
