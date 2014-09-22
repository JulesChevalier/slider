package fr.ujm.tse.lt2c.satin.slider.buffer;

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

import java.util.HashMap;
import java.util.TimerTask;

import fr.ujm.tse.lt2c.satin.slider.rules.Rule;

/**
 * @author Jules Chevalier
 *
 */
public class BufferTimer extends TimerTask {

    public static final long DEFAULT_TIMEOUT = 500;

    private final HashMap<Rule, Long> rulesLastFlushes;
    private final long timeout;

    public BufferTimer(final long timeout) {
        super();
        this.rulesLastFlushes = new HashMap<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {
        final Long now = System.nanoTime();
        Long lastAdd;
        for (final Rule rule : this.rulesLastFlushes.keySet()) {
            lastAdd = (now - this.rulesLastFlushes.get(rule)) / 1000000;
            if (lastAdd > this.timeout && rule.getTripleBuffer().getOccupation() > 0) {
                rule.bufferFull();
            }
        }

    }

    public void notifyAdd(final Rule rule) {
        final long now = System.nanoTime();
        this.rulesLastFlushes.put(rule, now);
    }

    public void addRule(final Rule rule) {
        this.notifyAdd(rule);
    }
}
