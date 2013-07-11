package fr.ujm.tse.lt2c.satin.mark1.rules;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import fr.ujm.tse.lt2c.satin.interfaces.Dictionnary;
import fr.ujm.tse.lt2c.satin.interfaces.Rule;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.naiveImpl.TripleImplNaive;

public class Mark1EQ_REP_S implements Rule {

	private static Logger logger = Logger.getLogger(Mark1EQ_REP_S.class);
	private Dictionnary dictionnary;
	private TripleStore tripleStore;
	private Collection<Triple> usableTriples;
	Collection<Triple> newTriples;

	public Mark1EQ_REP_S(Dictionnary dictionnary, Collection<Triple> usableTriples,  Collection<Triple> newTriples, TripleStore tripleStore) {
		super();
		this.dictionnary = dictionnary;
		this.tripleStore = tripleStore;
		this.usableTriples = usableTriples;
		this.newTriples = newTriples;
	}

	@Override
	public void run() {


		/**
		 * 	INPUT
		 * s owl:sameAs s'
		 * s p o
		 *  OUPUT
		 * s' p o
		 */

		/*
		 * Get concepts codes in dictionnary
		 */
		long sameAs = dictionnary.add("http://www.w3.org/2002/07/owl#sameAs");
		
		long loops = 0;

		/*
		 * Get triples matching input
		 */
		Collection<Triple> sameAs_Triples = tripleStore.getbyPredicate(sameAs);
		Collection<Triple> outputTriples = new HashSet<>();

		if (usableTriples == null) { // We use the entire triplestore

			for (Triple t1 : sameAs_Triples) {
				long s1=t1.getSubject(), o1=t1.getObject();
				
				/*
				 * Optimisation : Don't work on <A sameAs A> triples
				 */
				if(s1!=o1){
					
					for (Triple t2 : tripleStore.getAll()) {
						long s2=t2.getSubject(), p2=t2.getPredicate(), o2=t2.getObject();

						loops++;
						if(s1==s2){
							Triple result = new TripleImplNaive(o1,p2,o2);
							logger.trace("F EQ_REP_S "+dictionnary.printTriple(t1)+" + "+dictionnary.printTriple(t2)+" -> "+dictionnary.printTriple(result));
							outputTriples.add(result);
						}
					}
				}
			}
		}
		else{ //There are usable triples, so we just manage with them

			for (Triple t1 : usableTriples) {
				long s1 = t1.getSubject(), p1=t1.getPredicate(), o1 = t1.getObject();
				
				for (Triple t2 : tripleStore.getAll()) {
					long s2 = t2.getSubject(), p2=t2.getPredicate(), o2 = t2.getObject();
					loops++;
					
					/*
					 * Optimisation : Don't work on <A sameAs A> triples
					 */
					if(p1==sameAs && s1!=o1 && s1==s2){
						Triple result = new TripleImplNaive(o1, p2, o2);
						logger.trace("PRP_DOM " + dictionnary.printTriple(t1)+ " & " + dictionnary.printTriple(t2) + " -> "+ dictionnary.printTriple(result));
						outputTriples.add(result);
					}
					if(p2==sameAs && s2!=o2 && s2==s1){
						Triple result = new TripleImplNaive(o2, p1, o1);
						logger.trace("PRP_DOM " + dictionnary.printTriple(t2)+ " & " + dictionnary.printTriple(t1) + " -> "+ dictionnary.printTriple(result));
						outputTriples.add(result);						
					}
				}
			}

		}
		for (Triple triple : outputTriples) {
			if(!tripleStore.getAll().contains(triple)){
				tripleStore.add(triple);
				newTriples.add(triple);
				
			}
		}
//		tripleStore.addAll(outputTriples);
//		newTriples.addAll(outputTriples);
		logger.debug(this.getClass()+" : "+loops+" itérations");
	}

}
