package fr.ujm.tse.lt2c.satin.slider.interfaces;

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

import com.google.common.collect.Multimap;

/**
 * @author Jules Chevalier
 * 
 *         Interface of a triplestore
 *         Use Triple interface
 * @see Triple
 */
public interface TripleStore {

    /**
     * Add the triple t to the TripleStore
     * 
     * @param t
     * @return true if the triple is not in the triple store yet, false otherwise
     * @see Triple
     */
    boolean add(Triple t);

    /**
     * Add the triple <s,p,o> to the TripleStore
     * 
     * @param s
     * @param p
     * @param o
     */
    void add(long s, long p, long o);

    /**
     * Calls add for each triple in the collection
     * 
     * @param t
     * @return A collection with all the added triples
     * @see Triple
     * @see #add(Triple)
     */
    Collection<Triple> addAll(Collection<Triple> t);

    /**
     * Removes the triple t from the TripleStore
     * Does nothing if it does not contains t
     * 
     * @param t
     * @see Triple
     */
    void remove(Triple t);

    /**
     * @return all the triples in the TripleStore in a Collection
     * @see Triple
     */
    Collection<Triple> getAll();

    /**
     * @param s
     * @return the triples with s as subject, as a collection
     * @see Triple
     */
    Collection<Triple> getbySubject(long s);

    /**
     * @param p
     * @return the triples with p as predicate, as a collection
     * @see Triple
     */
    Collection<Triple> getbyPredicate(long p);

    /**
     * @param o
     * @return the triples with o as object, as a collection
     * @see Triple
     */
    Collection<Triple> getbyObject(long o);

    /**
     * @return the number of triples in the TripleStore
     */
    long size();

    /**
     * Writes the TripleStore in a file in a defined format
     * 
     * @param file
     * @param dictionary
     * @see Dictionary
     */
    void writeToFile(String file, Dictionary dictionary);

    /**
     * @return true if the TripleStore is empty, false else
     */
    boolean isEmpty();

    /**
     * @param triple
     * @return true if the TripleStore contains triple, false else
     * @see Triple
     */
    boolean contains(Triple triple);

    /**
     * @param s
     * @param p
     * @param o
     * @return true if the TripleStore contains triple <s,p,o>, false else
     */
    boolean contains(long s, long p, long o);

    /**
     * Remove all the triples
     */
    void clear();

    /**
     * @param p
     * @return the triples with p as predicate, as a Multimap
     * @see Multimap
     */
    Multimap<Long, Long> getMultiMapForPredicate(long p);

    /**
     * @return all the predicates in a Collection
     */
    Collection<Long> getPredicates();

}
