package fr.ujm.tse.lt2c.satin.slider.rules.run;

/*
 * #%L
 * SLIDeR
 * %%
 * Copyright (C) 2014 Université Jean Monnet, Saint Etienne
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import fr.ujm.tse.lt2c.satin.slider.buffer.TripleDistributor;
import fr.ujm.tse.lt2c.satin.slider.dictionary.AbstractDictionary;
import fr.ujm.tse.lt2c.satin.slider.interfaces.Dictionary;
import fr.ujm.tse.lt2c.satin.slider.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.slider.interfaces.TripleBuffer;
import fr.ujm.tse.lt2c.satin.slider.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.slider.triplestore.ImmutableTriple;

/**
 * INPUT
 * c1 rdfs:subClassOf c2
 * c2 rdfs:subClassOf c3
 * OUPUT
 * c1 rdfs:subClassOf c3
 * 
 * @author Jules Chevalier
 *
 */
public class RunSCM_SCO extends AbstractRun {

    private static final Logger LOGGER = Logger.getLogger(RunSCM_SCO.class);
    private static final String RULENAME = "SCM_SCO";
    public static final long[] INPUT_MATCHERS = { AbstractDictionary.subClassOf };
    public static final long[] OUTPUT_MATCHERS = { AbstractDictionary.subClassOf };

    public RunSCM_SCO(final Dictionary dictionary, final TripleStore tripleStore, final TripleBuffer tripleBuffer, final TripleDistributor tripleDistributor,
            final AtomicInteger phaser) {
        super(dictionary, tripleStore, tripleBuffer, tripleDistributor, phaser);
        super.ruleName = RULENAME;

    }

    @Override
    protected int process(final TripleStore ts1, final TripleStore ts2, final Collection<Triple> outputTriples) {

        final long subClassOf = AbstractDictionary.subClassOf;

        int loops = 0;

        final Multimap<Long, Long> subclassMultimap = ts1.getMultiMapForPredicate(subClassOf);
        if (subclassMultimap != null && !subclassMultimap.isEmpty()) {

            final Collection<Triple> subclassTriples = ts2.getbyPredicate(subClassOf);

            final Map<Long, Collection<Long>> cachePredicates = new HashMap<>();

            /* For each type triple */
            for (final Triple triple : subclassTriples) {
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
                for (final Long c1a : c3s) {

                    if (c1a != triple.getSubject()) {
                        final Triple result = new ImmutableTriple(triple.getSubject(), subClassOf, c1a);
                        outputTriples.add(result);
                    }

                }
            }
        }

        return loops;

    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String toString() {
        return RULENAME;
    }

}
