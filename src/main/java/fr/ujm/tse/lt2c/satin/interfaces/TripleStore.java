package fr.ujm.tse.lt2c.satin.interfaces;

import java.util.Collection;

import com.google.common.collect.Multimap;

/**
 * @author jules
 * 
 *         Interface of a triplestore
 *         Use Triple interface
 * @see Triple
 */
public interface TripleStore {

    /**
     * Add the triple to the TripleStore
     * 
     * @param t
     * @see Triple
     */
    void add(Triple t);

    /**
     * Calls add for each triple in the collection
     * 
     * @param t
     * @see Triple
     * @see #add(Triple)
     */
    void addAll(Collection<Triple> t);

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
     * Writes the TripleStore in a file in a RAW format
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
     * Remove all the triples
     */
    void clear();

    /**
     * Optional operation
     * 
     * @param p
     * @return the triples with p as predicate, as a multimap
     */
    Multimap<Long, Long> getMultiMapForPredicate(long p);

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

}
