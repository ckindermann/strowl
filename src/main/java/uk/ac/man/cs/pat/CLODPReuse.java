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


/**
 * Created by chris on 20/08/20.
 */

public class CLODPReuse {

    private OWLClass entity;

    private Set<OWLAxiom> diseaseModel;
    private String diseaseModelIRI = "http://purl.obolibrary.org/obo/CLO_0000179";

    private Set<OWLAxiom> specifiedInput;
    private String specifiedInputIRI = "http://purl.obolibrary.org/obo/OBI_0000295";

    private Set<OWLAxiom> derivesFromPatient;
    private String derivesFromPatientIRI = "http://purl.obolibrary.org/obo/CLO_0000015";

    private Set<OWLAxiom> derivesFrom;
    private String derivesFromIRI = "http://purl.obolibrary.org/obo/RO_0001000";

    private Set<OWLAxiom> isInCellLineRepository;
    private String isInCellLineRepositoryIRI = "http://purl.obolibrary.org/obo/CLO_0000174";

    private Set<OWLAxiom> union;

    private Map<OWLAxiom,Integer> axiom2repetition;

    public CLODPReuse(OWLClass e, Set<OWLAxiom> usage){
        this(e);
        this.setReuse(usage); 
    }

    public CLODPReuse(OWLClass e){
        this.entity = e;
        this.diseaseModel = new HashSet<>();
        this.specifiedInput = new HashSet<>();
        this.derivesFromPatient = new HashSet<>();
        this.derivesFrom = new HashSet<>();
        this.isInCellLineRepository = new HashSet<>();
    }

    public Set<OWLAxiom> getDiseaseModel(){
        return this.diseaseModel;
    }
    public Set<OWLAxiom> getSpecifiedInput(){
        return this.specifiedInput;
    }
    public Set<OWLAxiom> getDerivesFromPatient(){
        return this.derivesFromPatient;
    }
    public Set<OWLAxiom> getDerivesFrom(){
        return this.derivesFrom;
    }
    public Set<OWLAxiom> getInCellLineRepository(){
        return this.isInCellLineRepository;
    }

    public Set<OWLAxiom> getAxioms(){
        return this.union;
    }

    public void setReuse(Set<OWLAxiom> usage){
        for(OWLAxiom a : usage){
            if(a instanceof OWLSubClassOfAxiom){
                OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) a;
                OWLClassExpression rhs =  axiom.getSuperClass(); 

                if(rhs instanceof OWLObjectSomeValuesFrom){
                    OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) rhs;
                    String property = some.getProperty().asOWLObjectProperty().getIRI().toString();
                    if(property.contains(diseaseModelIRI)){
                        this.diseaseModel.add(a);
                    } 
                    if(property.contains(specifiedInputIRI)){
                        this.specifiedInput.add(a);
                    } 
                    if(property.contains(derivesFromPatientIRI)){
                        this.derivesFromPatient.add(a);
                    } 
                    if(property.contains(derivesFromIRI)){
                        this.derivesFrom.add(a);
                    } 
                }

                if(rhs instanceof OWLObjectHasValue){
                    OWLObjectHasValue value = (OWLObjectHasValue) rhs;
                    String property = value.getProperty().asOWLObjectProperty().getIRI().toString();
                    if(property.contains(isInCellLineRepositoryIRI)){ 
                        this.isInCellLineRepository.add(a);
                    } 
                }
            }
        }
        this.union = new HashSet<>();
        this.union.addAll(this.diseaseModel);
        this.union.addAll(this.specifiedInput);
        this.union.addAll(this.derivesFrom);
        this.union.addAll(this.derivesFromPatient);
        this.union.addAll(this.isInCellLineRepository);
    } 

    public boolean hasRepetition(){
        if(this.diseaseModel.size() > 1)
            return true;
        if(this.specifiedInput.size() > 1)
            return true;
        if(this.derivesFromPatient.size() > 1)
            return true;
        if(this.derivesFrom.size() > 1)
            return true;
        if(this.isInCellLineRepository.size() > 1)
            return true;
        return false; 
    }


    public boolean hasDiseaseModelRepetition(){
        return this.diseaseModel.size() > 1;
    } 
    public boolean hasSpecifiedInputRepetition(){
        return this.specifiedInput.size() > 1;
    } 
    public boolean hasDerivesFromPatientRepetition(){
        return this.derivesFromPatient.size() > 1;
    }
    public boolean hasDerivesFromRepetition(){
        return this.derivesFrom.size() > 1;
    }
    public boolean hasCellLineRepistoryRepetition(){
        return this.isInCellLineRepository.size() > 1;
    }

    public boolean isEmpty(){
        if(!this.diseaseModel.isEmpty())
            return false;
        if(!this.specifiedInput.isEmpty())
            return false;
        if(!this.derivesFromPatient.isEmpty())
            return false;
        if(!this.derivesFrom.isEmpty())
            return false;
        if(!this.isInCellLineRepository.isEmpty())
            return false;
        return true; 
    }

    public boolean fullReuse(){
        if(this.diseaseModel.isEmpty())
            return false;
        if(this.specifiedInput.isEmpty())
            return false;
        if(this.derivesFromPatient.isEmpty())
            return false;
        if(this.derivesFrom.isEmpty())
            return false;
        if(this.isInCellLineRepository.isEmpty())
            return false;
        return true; 
    }

    public int size(){
        return this.union.size();
    }

    public void print(){
        System.out.println("For entity " + this.entity.toString());
        for(OWLAxiom a : this.union){
            System.out.println(a.toString());
        } 
    }
}
