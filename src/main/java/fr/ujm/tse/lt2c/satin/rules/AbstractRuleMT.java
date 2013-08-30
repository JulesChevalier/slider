package fr.ujm.tse.lt2c.satin.rules;

import java.util.Collection;

import fr.ujm.tse.lt2c.satin.interfaces.Dictionnary;
import fr.ujm.tse.lt2c.satin.interfaces.Rule;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;

public abstract class AbstractRuleMT implements Rule {

	protected Dictionnary dictionnary;
	protected TripleStore tripleStore;
	protected TripleStore usableTriples;
	protected Collection<Triple> newTriples;
	protected String ruleName = "";

	protected void addNewTriples(Collection<Triple> outputTriples) {
		for (Triple triple : outputTriples) {
			if (!tripleStore.contains(triple)) {
				tripleStore.add(triple);
				synchronized (newTriples) {
					newTriples.add(triple);
				}
			} else {
				logTrace(dictionnary.printTriple(triple) + " allready present");
			}

		}

	}

	protected void logDebug(String message) {
		if (getLogger().isDebugEnabled()) {
			getLogger()
					.debug((usableTriples.isEmpty() ? "F " + ruleName + " "
							: ruleName) + message);
		}
	}

	protected void logTrace(String message) {
		if (getLogger().isTraceEnabled()) {
			getLogger().trace(
					(usableTriples.isEmpty() ? "F " + ruleName + " " : ruleName
							+ " ")
							+ message);
		}
	}

}
