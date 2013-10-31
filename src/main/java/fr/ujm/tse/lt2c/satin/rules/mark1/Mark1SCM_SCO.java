package fr.ujm.tse.lt2c.satin.rules.mark1;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import fr.ujm.tse.lt2c.satin.buffer.TripleDistributor;
import fr.ujm.tse.lt2c.satin.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Dictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleBuffer;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.rules.AbstractRule;
import fr.ujm.tse.lt2c.satin.triplestore.ImmutableTriple;

/**
 * INPUT
 * c1 rdfs:subClassOf c2
 * c2 rdfs:subClassOf c3
 * OUPUT
 * c1 rdfs:subClassOf c3
 */
public class Mark1SCM_SCO extends AbstractRule {

	private static Logger logger = Logger.getLogger(Mark1SCM_SCO.class);
	public static long[] matchers = {AbstractDictionary.subClassOf};

	public Mark1SCM_SCO(Dictionary dictionary, TripleStore tripleStore, CountDownLatch doneSignal, TripleDistributor distributor, TripleBuffer tripleBuffer) {
		super(dictionary, tripleStore, "SCM_SCO", doneSignal, distributor, tripleBuffer);

	}

	protected int process(TripleStore ts1, TripleStore ts2, Collection<Triple> outputTriples) {

		long subClassOf = AbstractDictionary.subClassOf;

		int loops = 0;

		Multimap<Long, Long> subclassMultimap = ts1.getMultiMapForPredicate(subClassOf);
		if (subclassMultimap != null && subclassMultimap.size() > 0) {

			Collection<Triple> subclassTriples = ts2.getbyPredicate(subClassOf);

			HashMap<Long, Collection<Long>> cachePredicates = new HashMap<>();

			/* For each type triple */
			for (Triple triple : subclassTriples) {
				/*
				 * Get all objects (c1a) of subClassOf triples with
				 */

				Collection<Long> c3s;
				if (!cachePredicates.containsKey(triple.getObject())) {
					c3s = subclassMultimap.get(triple.getObject());
					cachePredicates.put(triple.getObject(), c3s);
				} else {
					c3s = cachePredicates.get(triple.getObject());
				}

				loops++;
				for (Long c1a : c3s) {

					if (c1a != triple.getSubject()) {

						Triple result = new ImmutableTriple(triple.getSubject(), subClassOf, c1a);
						outputTriples.add(result);

						logTrace(dictionary.printTriple(new ImmutableTriple(triple.getSubject(), subClassOf, triple.getObject())) + " & " + dictionary.printTriple(new ImmutableTriple(triple.getObject(), subClassOf, triple.getSubject())) + " -> " + dictionary.printTriple(result));
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
