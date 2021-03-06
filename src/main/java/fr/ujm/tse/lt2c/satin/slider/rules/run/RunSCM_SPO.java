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
 * p3 rdfs:subPropertyOf p2
 * p2 rdfs:subPropertyOf p3
 * OUPUT
 * p3 rdfs:subPropertyOf p3
 * 
 * @author Jules Chevalier
 *
 */
public class RunSCM_SPO extends AbstractRun {

    private static final Logger LOGGER = Logger.getLogger(RunSCM_SPO.class);
    private static final String RULENAME = "SCM_SPO";
    public static final long[] INPUT_MATCHERS = { AbstractDictionary.subPropertyOf };
    public static final long[] OUTPUT_MATCHERS = { AbstractDictionary.subPropertyOf };

    public RunSCM_SPO(final Dictionary dictionary, final TripleStore tripleStore, final TripleBuffer tripleBuffer, final TripleDistributor tripleDistributor,
            final AtomicInteger phaser) {
        super(dictionary, tripleStore, tripleBuffer, tripleDistributor, phaser);
        super.ruleName = RULENAME;

    }

    @Override
    protected int process(final TripleStore ts1, final TripleStore ts2, final Collection<Triple> outputTriples) {

        final long subPropertyOf = AbstractDictionary.subPropertyOf;

        int loops = 0;

        final Multimap<Long, Long> subpropertyMultimap = ts1.getMultiMapForPredicate(subPropertyOf);
        if (subpropertyMultimap != null && !subpropertyMultimap.isEmpty()) {

            final Collection<Triple> subpropertyTriples = ts2.getbyPredicate(subPropertyOf);

            final Map<Long, Collection<Long>> cachePredicates = new HashMap<>();

            /* For each type triple */
            for (final Triple triple : subpropertyTriples) {
                /*
                 * Get all objects (p3) of subPropertyOf triples with
                 */

                Collection<Long> p3s;
                if (!cachePredicates.containsKey(triple.getObject())) {
                    p3s = subpropertyMultimap.get(triple.getObject());
                    cachePredicates.put(triple.getObject(), p3s);
                } else {
                    p3s = cachePredicates.get(triple.getObject());
                }

                loops++;
                for (final Long p3 : p3s) {

                    if (p3 != triple.getSubject()) {

                        final Triple result = new ImmutableTriple(triple.getSubject(), subPropertyOf, p3);
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
