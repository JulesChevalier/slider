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

import org.apache.log4j.Logger;

/**
 * Baseline of the reasoner. Compute the new triples generated by a rule
 * e.g. <c1 rdfs:subClassOf c2> & <x rdf:type c1> => <x rdf:type c2>
 * 
 * @author Jules Chevalier
 */
public interface RuleRun extends Runnable {

    /**
     * @return the logger of the class
     */
    Logger getLogger();

}
