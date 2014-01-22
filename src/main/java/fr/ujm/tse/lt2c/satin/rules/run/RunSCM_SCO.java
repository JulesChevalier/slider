package fr.ujm.tse.lt2c.satin.rules.run;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Phaser;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import fr.ujm.tse.lt2c.satin.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Dictionary;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.triplestore.ImmutableTriple;

/**
 * INPUT
 * c1 rdfs:subClassOf c2
 * c2 rdfs:subClassOf c3
 * OUPUT
 * c1 rdfs:subClassOf c3
 */
public class RunSCM_SCO extends AbstractRun {

    private static final Logger logger = Logger.getLogger(RunSCM_SCO.class);
    public static final long[] INPUT_MATCHERS = { AbstractDictionary.subClassOf };
    public static final long[] OUTPUT_MATCHERS = { AbstractDictionary.subClassOf };

    public RunSCM_SCO(Dictionary dictionary, TripleStore tripleStore, Phaser phaser) {
        super(dictionary, tripleStore, phaser, "SCM_SCO");

    }

    @Override
    protected int process(TripleStore ts1, TripleStore ts2, Collection<Triple> outputTriples) {

        long subClassOf = AbstractDictionary.subClassOf;

        int loops = 0;

        Multimap<Long, Long> subclassMultimap = ts1.getMultiMapForPredicate(subClassOf);
        if (subclassMultimap != null && !subclassMultimap.isEmpty()) {

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

                        logTrace(dictionary.printTriple(new ImmutableTriple(triple.getSubject(), subClassOf, triple.getObject())) + " & " + dictionary.printTriple(new ImmutableTriple(triple.getObject(), subClassOf, c1a)) + " -> " + dictionary.printTriple(result));
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

    @Override
    public long[] getInputMatchers() {
        return INPUT_MATCHERS;
    }

    @Override
    public long[] getOutputMatchers() {
        return OUTPUT_MATCHERS;
    }

}
