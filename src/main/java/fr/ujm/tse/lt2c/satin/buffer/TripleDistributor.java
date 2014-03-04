package fr.ujm.tse.lt2c.satin.buffer;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleBuffer;

public class TripleDistributor {

    private final Multimap<Long, TripleBuffer> subscribers;
    private final Collection<TripleBuffer> universalSubscribers;
    private String debugName;

    /**
     * Constructor
     */
    public TripleDistributor() {
        super();
        this.subscribers = HashMultimap.create();
        this.universalSubscribers = new HashSet<>();
        this.debugName = "";
    }

    /**
     * Add a TripleBuffer as subscribers for predicates
     * 
     * @param tripleBuffer
     * @param predicates
     * @see TripleBuffer
     */
    public void addSubscriber(final TripleBuffer tripleBuffer, final long[] predicates) {
        if (predicates.length == 0) {
            if (!this.universalSubscribers.contains(tripleBuffer)) {
                this.universalSubscribers.add(tripleBuffer);
            }
            return;
        }
        for (final Long predicate : predicates) {
            if (!this.subscribers.containsEntry(predicate, tripleBuffer)) {
                this.subscribers.put(predicate, tripleBuffer);
            }
        }
    }

    /**
     * Send all the triples to all matching subscribers
     * 
     * @param triples
     * @see Triple
     */
    public void distributeAll(final Collection<Triple> triples) {
        for (final Triple triple : triples) {
            this.ditribute(triple);
        }
    }

    public long ditribute(final Triple triple) {
        long debugDistributed = 0;
        for (final TripleBuffer tripleBuffer : this.subscribers.get(triple.getPredicate())) {
            tripleBuffer.add(triple);
            debugDistributed++;
        }
        for (final TripleBuffer tripleBuffer : this.universalSubscribers) {
            tripleBuffer.add(triple);
            debugDistributed++;
        }
        return debugDistributed;
    }

    /**
     * @param name
     */
    public void setName(final String name) {
        this.debugName = name;
    }

    /**
     * @return the number of subscribers
     */
    public int subscribersNumber() {
        return this.subscribers.values().size() + this.universalSubscribers.size();
    }

}
