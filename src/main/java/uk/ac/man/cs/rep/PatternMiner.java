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
 * Created by chris on 20/08/20.
 */

public class PatternMiner {
    private MaterialisedClassHierarchy matClassHierarchy;

    public PatternMiner(OWLOntology o){
        this.matClassHierarchy = new MaterialisedClassHierarchy(o);
    }

}
