package fr.ujm.tse.lt2c.satin.slider.utils;

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
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.ujm.tse.lt2c.satin.slider.interfaces.Dictionary;
import fr.ujm.tse.lt2c.satin.slider.interfaces.Parser;
import fr.ujm.tse.lt2c.satin.slider.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.slider.reasoner.IncrementalReasoner;
import fr.ujm.tse.lt2c.satin.slider.triplestore.ImmutableTriple;

/**
 * @author Jules Chevalier
 *
 */
public class ParserImplNaive implements Parser {

    private final Dictionary dictionary;
    private final TripleStore tripleStore;
    private static final int STREAM_BLOCK_SIZE = 1000;

    /**
     * @param dictionary
     * @param tripleStore
     */
    public ParserImplNaive(final Dictionary dictionary, final TripleStore tripleStore) {
        this.dictionary = dictionary;
        this.tripleStore = tripleStore;
    }

    @Override
    public Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> parse(final String fileInput) {

        final PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);
        final Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> triples = new HashSet<>();

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Runnable parser = new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("Parser");
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, fileInput);
            }
        };

        executor.submit(parser);
        executor.shutdown();
        while (iter.hasNext()) {
            final Triple next = iter.next();
            final Node s = next.getSubject();
            final Node p = next.getPredicate();
            final Node o = next.getObject();

            final long si = this.dictionary.add(s);
            final long pi = this.dictionary.add(p);
            final long oi = this.dictionary.add(o);

            final fr.ujm.tse.lt2c.satin.slider.interfaces.Triple triple = new ImmutableTriple(si, pi, oi);
            if (!this.tripleStore.add(triple)) {
                triples.add(triple);
            }
        }

        synchronized (this.dictionary) {
            this.dictionary.notify();
        }

        return triples;
    }

    @Override
    public int parseStream(final String fileInput, final IncrementalReasoner reasoner) {

        final PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Runnable parser = new Runnable() {

            @Override
            public void run() {
                // final Call the parsing process.
                RDFDataMgr.parse(inputStream, fileInput);
            }
        };

        executor.submit(parser);
        executor.shutdown();

        final Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> triples = new HashSet<>();
        final Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> toAdd = new HashSet<>();
        int totalInput = 0;
        while (iter.hasNext()) {

            final Triple next = iter.next();
            final Node s = next.getSubject();
            final Node p = next.getPredicate();
            final Node o = next.getObject();

            final long si = this.dictionary.add(s);
            final long pi = this.dictionary.add(p);
            final long oi = this.dictionary.add(o);
            final fr.ujm.tse.lt2c.satin.slider.interfaces.Triple triple = new ImmutableTriple(si, pi, oi);

            triples.add(triple);
            if (triples.size() >= STREAM_BLOCK_SIZE) {
                toAdd.addAll(this.tripleStore.addAll(triples));
                totalInput += toAdd.size();
                reasoner.addTriples(toAdd);
                triples.clear();
                toAdd.clear();
            }
        }
        totalInput += triples.size();
        this.tripleStore.addAll(triples);
        reasoner.addTriples(triples);

        synchronized (this.dictionary) {
            this.dictionary.notify();
        }

        return totalInput;
    }

    @Override
    public Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> parse(final Model model) {
        final StmtIterator smtIterator = model.listStatements();
        final Collection<fr.ujm.tse.lt2c.satin.slider.interfaces.Triple> triples = new HashSet<>();

        while (smtIterator.hasNext()) {
            final Triple next = smtIterator.next().asTriple();
            final Node s = next.getSubject();
            final Node p = next.getPredicate();
            final Node o = next.getObject();

            final long si = this.dictionary.add(s);
            final long pi = this.dictionary.add(p);
            final long oi = this.dictionary.add(o);

            final fr.ujm.tse.lt2c.satin.slider.interfaces.Triple triple = new ImmutableTriple(si, pi, oi);
            if (!this.tripleStore.add(triple)) {
                triples.add(triple);
            }
        }

        synchronized (this.dictionary) {
            this.dictionary.notify();
        }
        return triples;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.dictionary == null ? 0 : this.dictionary.hashCode());
        result = prime * result + (this.tripleStore == null ? 0 : this.tripleStore.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ParserImplNaive other = (ParserImplNaive) obj;
        if (this.dictionary == null) {
            if (other.dictionary != null) {
                return false;
            }
        } else if (!this.dictionary.equals(other.dictionary)) {
            return false;
        }
        if (this.tripleStore == null) {
            if (other.tripleStore != null) {
                return false;
            }
        } else if (!this.tripleStore.equals(other.tripleStore)) {
            return false;
        }
        return true;
    }
}
