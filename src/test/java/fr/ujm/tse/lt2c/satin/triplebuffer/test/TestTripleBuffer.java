package fr.ujm.tse.lt2c.satin.triplebuffer.test;

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

import org.junit.Test;

import fr.ujm.tse.lt2c.satin.slider.interfaces.BufferListener;
import fr.ujm.tse.lt2c.satin.slider.interfaces.TripleBuffer;

public class TestTripleBuffer {

    public static final int PROBA = 100;
    public static final int BUFFER_SIZE = 10;
    public static final int MAX_VALUE = 1000;

    @Test
    public void basicTest() {
        // final TripleBuffer tb = new QueuedTripleBufferLock(1);
        // tb.add(new ImmutableTriple(0, 0, 0));
        // tb.add(new ImmutableTriple(0, 0, 0));
        // tb.add(new ImmutableTriple(0, 0, 0));
        // tb.add(new ImmutableTriple(0, 0, 0));
        // Assert.assertEquals(4, tb.getOccupation());
        // Assert.assertEquals(1, tb.clear().size());
        // Assert.assertEquals(3, tb.getOccupation());
    }

    @Test
    public void test() {
        // TripleBuffer tb = new QueuedTripleBufferLock(BUFFER_SIZE);
        // final Set<Triple> generated = new HashSet<>();
        //
        // // Test buffer flush
        // final Random random = new Random();
        // while (generated.size() < (tb.getBufferLimit() - 1)) {
        // final Triple t = new ImmutableTriple(random.nextInt(PROBA), random.nextInt(PROBA), random.nextInt(PROBA));
        // tb.add(t);
        // generated.add(t);
        // }
        // assertEquals(generated.size(), tb.getOccupation());
        //
        // // ----Clear test
        // final SimpleBufferListener sbl = new SimpleBufferListener(tb);
        // tb.addBufferListener(sbl);
        // assertEquals(1, tb.getBufferListeners().size());
        //
        // // Switchy must occur here
        // tb.add(new ImmutableTriple(random.nextInt(PROBA), random.nextInt(PROBA), random.nextInt(PROBA)));
        // assertEquals(0, tb.getOccupation());
        //
        // // ---- Overflow test
        // tb = new QueuedTripleBufferLock(BUFFER_SIZE);
        // final OverFlowListener ofl = new OverFlowListener(tb);
        // tb.addBufferListener(ofl);
        // assertEquals(1, tb.getBufferListeners().size());
        // generated.clear();
        //
        // while (generated.size() < ((tb.getBufferLimit() * 3) + 3)) {
        // final Triple t = new ImmutableTriple(random.nextInt(PROBA), random.nextInt(PROBA), random.nextInt(PROBA));
        // tb.add(t);
        // generated.add(t);
        // }
        // assertEquals(3, tb.getOccupation());

    }

    class SimpleBufferListener implements BufferListener {

        TripleBuffer tb;

        public SimpleBufferListener(final TripleBuffer tb) {
            super();
            this.tb = tb;
        }

        @Override
        public boolean bufferFull() {
            this.tb.clear();
            return true;
        }

        @Override
        public boolean bufferFullTimer(final long triplesToRead) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    class OverFlowListener implements BufferListener {

        TripleBuffer tb;

        public OverFlowListener(final TripleBuffer tb) {
            super();
            this.tb = tb;
        }

        @Override
        public boolean bufferFull() {
            // Do shit
            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            this.tb.clear();
            return true;
        }

        @Override
        public boolean bufferFullTimer(final long triplesToRead) {
            // TODO Auto-generated method stub
            return false;
        }

    }
}
