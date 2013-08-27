package fr.ujm.tse.lt2c.satin.rules.mark1;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.dictionnary.AbstractDictionnary;
import fr.ujm.tse.lt2c.satin.interfaces.Dictionnary;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.rules.AbstractRule;
import fr.ujm.tse.lt2c.satin.triplestore.TripleImplNaive;

public class Mark1SCM_SCO extends AbstractRule {

	private static Logger logger = Logger.getLogger(Mark1SCM_SCO.class);

	public Mark1SCM_SCO(Dictionnary dictionnary, TripleStore usableTriples,  Collection<Triple> newTriples, TripleStore tripleStore) {
		super();
		this.dictionnary = dictionnary;
		this.tripleStore = tripleStore;
		this.usableTriples = usableTriples;
		this.newTriples = newTriples;
		this.ruleName = "SCM_SCO";
	}

	@Override
	public void run() {


		/**
		 * 	INPUT
		 * c1 rdfs:subClassOf c2
		 * c2 rdfs:subClassOf c3
		 *  OUPUT
		 * c1 rdfs:subClassOf c3
		 */

		/*
		 * Get concepts codes in dictionnary
		 */
		long subClassOf = AbstractDictionnary.subClassOf;

		long loops = 0;

		/*
		 * Get triples matching input 
		 * Create
		 */
		Collection<Triple> outputTriples = new HashSet<>();

		Collection<Triple> subClassOf_Triples = tripleStore.getbyPredicate(subClassOf);

		if (usableTriples.isEmpty()) { // We use the entire triplestore

			for (Triple t1 : subClassOf_Triples) {
				long s1=t1.getSubject(), o1=t1.getObject();

				for (Triple t2 : subClassOf_Triples) {
					long s2=t2.getSubject(), o2=t2.getObject();

					loops++;
					if(o1==s2){
						Triple result = new TripleImplNaive(s1, subClassOf, o2);
						logTrace(dictionnary.printTriple(t1)+" & "+dictionnary.printTriple(t2)+" -> "+dictionnary.printTriple(result));
						outputTriples.add(result);
					}

				}

			}
		}else{ //There are usable triples, so we just manage with them

			for (Triple t1 : usableTriples.getAll()) {
				long s1 = t1.getSubject(), p1=t1.getPredicate(), o1 = t1.getObject();

				if(p1!=subClassOf)
					continue;

				for (Triple t2 : subClassOf_Triples) {
					long s2 = t2.getSubject(), o2 = t2.getObject();
					loops++;

					if(o1==s2){
						Triple result = new TripleImplNaive(s1, subClassOf, o2);
						logTrace(dictionnary.printTriple(t1)+ " & " + dictionnary.printTriple(t2) + " -> "+ dictionnary.printTriple(result));
						outputTriples.add(result);
					}
					if(o2==s1){
						Triple result = new TripleImplNaive(s2, subClassOf, o1);
						logTrace(dictionnary.printTriple(t1)+ " & " + dictionnary.printTriple(t2) + " -> "+ dictionnary.printTriple(result));
						outputTriples.add(result);
					}
				}
			}

		}

		addNewTriples(outputTriples);

		logDebug(this.getClass()+" : "+loops+" iterations");

	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
