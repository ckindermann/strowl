package uk.ac.man.cs.ont;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import java.util.*;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import java.io.File;

/**
 * Created by chris on 10/09/19.
 */
public class OntologySaver {

    public static void saveAxioms(Set<OWLAxiom> axioms, String outputPath) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
        OWLOntology ontology = manager.createOntology(); 
        File ontologyFile = new File(outputPath); 
        IRI documentIRI = IRI.create(ontologyFile.toURI()); 

        manager.addAxioms(ontology,axioms); 
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), documentIRI);
    }

    public static void saveClassAssertionAxioms(Set<OWLClassAssertionAxiom> axioms, String outputPath) throws Exception {
        Set<OWLAxiom> conversion = new HashSet<>();
        for(OWLClassAssertionAxiom a : axioms){
            conversion.add((OWLAxiom) a); 
        }
        OntologySaver.saveAxioms(conversion, outputPath);
    }

    public static void saveOntology(OWLOntology ontology, String outputPath) throws Exception {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
        File ontologyFile = new File(outputPath); 
        IRI documentIRI = IRI.create(ontologyFile.toURI()); 
        manager.saveOntology(ontology, new OWLXMLDocumentFormat(), documentIRI);
    }

    //public static OWLOntology generateOntology(Set<OWLAxiom> axioms) throws Exception {
    //    OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 
    //    OWLOntology ontology = manager.createOntology(); 
    //    manager.addAxioms(ontology,axioms); 
    //    return ontology; 
    //}

}
