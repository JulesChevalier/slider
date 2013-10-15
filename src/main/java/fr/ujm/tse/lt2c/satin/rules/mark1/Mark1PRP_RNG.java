package fr.ujm.tse.lt2c.satin.rules.mark1;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import fr.ujm.tse.lt2c.satin.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Dictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.rules.AbstractRule;
import fr.ujm.tse.lt2c.satin.triplestore.TripleImplNaive;

/**
 * INPUT
 * p rdfs:range c
 * x p y
 * OUPUT
 * y rdf:type c
 */
public class Mark1PRP_RNG extends AbstractRule {

	private static Logger logger = Logger.getLogger(Mark1PRP_RNG.class);

	public Mark1PRP_RNG(Dictionary dictionary, TripleStore usableTriples,Collection<Triple> newTriples, TripleStore tripleStore, CountDownLatch doneSignal) {
		super(dictionary,tripleStore,usableTriples,newTriples,"PRP_RNG",doneSignal);

	}
	
	@Override
	public void run() {

		try {

			long loops = 0;

			Collection<Triple> outputTriples = new HashSet<>();

			if (usableTriples.isEmpty()) {
				loops += process(tripleStore, tripleStore, outputTriples);
			} else {
				loops += process(usableTriples, tripleStore, outputTriples);
				loops += process(tripleStore, usableTriples, outputTriples);
			}

			addNewTriples(outputTriples);

			logDebug(this.getClass() + " : " + loops + " iterations - outputTriples  " + outputTriples.size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finish();

		}
	}

	private int process(TripleStore ts1, TripleStore ts2, Collection<Triple> outputTriples) {

		long range = AbstractDictionary.range;
		long type = AbstractDictionary.type;

		int loops = 0;

		Multimap<Long, Long> rangeMultiMap = ts1.getMultiMapForPredicate(range);
		if (rangeMultiMap != null && rangeMultiMap.size() > 0) {
			for (Long p : rangeMultiMap.keySet()) {
				Collection<Triple> matchingTriples = ts2.getbyPredicate(p);
				for (Triple triple : matchingTriples) {
					for (Long c : rangeMultiMap.get(p)) {
						Triple result = new TripleImplNaive(triple.getObject(), type, c);
						logTrace(dictionary.printTriple(triple)
								+ " & "
								+ dictionary.printTriple(new TripleImplNaive(p,range, c)) 
								+ " -> " 
								+ dictionary.printTriple(result));
						outputTriples.add(result);
					}

				}
			}
		}

		return loops;

	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
