package fr.ujm.tse.lt2c.satin.reasoner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ujm.tse.lt2c.satin.dictionnary.DictionnaryImplNaive;
import fr.ujm.tse.lt2c.satin.interfaces.Dictionnary;
import fr.ujm.tse.lt2c.satin.interfaces.Parser;
import fr.ujm.tse.lt2c.satin.interfaces.Rule;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.rules.AbstractRule;
import fr.ujm.tse.lt2c.satin.rules.mark1.Mark1PRP_DOM;
import fr.ujm.tse.lt2c.satin.rules.mark1.Mark1PRP_RNG;
import fr.ujm.tse.lt2c.satin.rules.mark1.Mark1PRP_SPO1;
import fr.ujm.tse.lt2c.satin.tools.ParserImplNaive;
import fr.ujm.tse.lt2c.satin.triplestore.TemporaryVerticalPartioningTripleStoreRWLock;
import fr.ujm.tse.lt2c.satin.triplestore.VerticalPartioningTripleStoreRWLock;

public class ReasonnerVerticalMTRWLock {

	public static CountDownLatch cdlWriter;
	private static Logger logger = Logger
			.getLogger(ReasonnerVerticalMTRWLock.class);
	private static ExecutorService executor;

	public static void main(String[] args) {

		CSVWriter writer = null;
		try {
			// writer = new CSVWriter(new FileWriter("resultsMTLock.csv"), ';');
			//
			// String[] headers = { "File", "i", "Initial triples",
			// "Infered triples", "Loops", "Time", "Left over" };
			// writer.writeNext(headers);

			for (int i = 0; i < 1000; i++) {

				// System.out.println("subclassof.owl 5618 bits");
				// infere("subclassof.owl", i, writer);
				// System.out.println();

				// System.out.println("sample1.owl 9714 bits");
				// infere("sample1.owl", i, writer);
				// System.out.println();

				// System.out.println("univ-bench.owl 13840 bits");
				// infere("univ-bench.owl", i, writer);
				// System.out.println();
				//
				// System.out.println("sweetAll.owl 17538 bits");
				// infere("sweetAll.owl", i, writer);
				// System.out.println();
				//
				// System.out.println("wine.rdf 78225 bits ("+i+")");
				infere("wine.rdf", i, writer);
				// System.out.println();
				//
				// System.out.println("geopolitical_200Ko.owl 199105 bits");
				// infere("geopolitical_200Ko.owl", i, writer);
				// System.out.println();
				//
				// System.out.println("geopolitical_300Ko.owl 306377 bits");
				// infere("geopolitical_300Ko.owl",i,writer);
				// System.out.println();
				//
				// System.out.println("geopolitical_500Ko.owl 497095 bits");
				// infere("geopolitical_500Ko.owl",i,writer);
				// System.out.println();
				//
				// System.out.println("geopolitical_1Mo.owl 1047485 bits");
				// infere("geopolitical_1Mo.owl",i,writer);
				// System.out.println();
				//
				// System.out.println("geopolitical.owl 1780714 bits");
				// infere("geopolitical.owl",i,writer);
				// System.out.println();
				//
				// System.out.println("efo.owl 26095973 bits");
				// infere("efo.owl",i,writer);
				// System.out.println();
				//
				// System.out.println("opencyc.owl 252122090 bits");
				// infere("opencyc.owl",i,writer);
				// System.out.println();
				//
			}

			// writer.close();
			// System.out.println("Work finished");

			Runtime.getRuntime().exec("text2speech \"The computation is over.\"");

			shutdownAndAwaitTermination(executor);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void infere(String input, int i, CSVWriter writer) {

		logger.debug("***************************************************************************************************************************************************************");
		logger.debug("****************************************************************************NEW RUN****************************************************************************");
		logger.debug("***************************************************************************************************************************************************************");

		TripleStore tripleStore = new VerticalPartioningTripleStoreRWLock();
		Dictionnary dictionnary = new DictionnaryImplNaive();
		Parser parser = new ParserImplNaive(dictionnary, tripleStore);

		// long startTime = System.nanoTime();

		parser.parse(input);

		// logger.debug("Parsing completed");

		// long parsingTime = System.nanoTime();

		long beginNbTriples = tripleStore.size();

		ArrayList<AbstractRule> rules = new ArrayList<>();
		TemporaryVerticalPartioningTripleStoreRWLock usableTriples = new TemporaryVerticalPartioningTripleStoreRWLock();
		Set<Triple> newTriples = Collections
				.newSetFromMap(new ConcurrentHashMap<Triple, Boolean>());

		CountDownLatch doneSignal = null;

		/* Initialize rules used for inference on RhoDF */

		// rules.add(new Mark1CAX_SCO(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		rules.add(new Mark1PRP_DOM(dictionnary, usableTriples, newTriples,
				tripleStore, doneSignal));
		rules.add(new Mark1PRP_RNG(dictionnary, usableTriples, newTriples,
				tripleStore, doneSignal));
		rules.add(new Mark1PRP_SPO1(dictionnary, usableTriples, newTriples,
				tripleStore, doneSignal));
		// rules.add(new Mark1SCM_SCO(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_EQC2(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_SPO(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_EQP2(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_DOM1(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_DOM2(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_RNG1(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));
		// rules.add(new Mark1SCM_RNG2(dictionnary, usableTriples, newTriples,
		// tripleStore, doneSignal));

		doneSignal = new CountDownLatch(rules.size());
		cdlWriter = new CountDownLatch(rules.size());

		for (AbstractRule r : rules)
			r.setDoneSignal(doneSignal);

		long old_size;
		long new_size;
		int steps = 0;

		executor = Executors.newFixedThreadPool(rules.size());

		do {
			old_size = tripleStore.size();
			long stepTime = System.nanoTime();
			logger.debug("--------------------STEP " + steps
					+ "--------------------" + doneSignal.getCount());

			for (Rule rule : rules) {
				executor.submit(rule);
			}
			// Wait all rules to finish
			// System.out.println("Waiting for latch");
			try {

				// for (AbstractRule r : rules) {
				// System.out.println(r.getRuleName() + " " + r.isFinished());
				// }
				// System.out.println(doneSignal.getCount());
				doneSignal.await();
				// System.out.println("######################################");
				// for (AbstractRule r : rules) {
				// System.out.println(r.getRuleName() + " " + r.isFinished());
				// }
				// System.out.println("Fire in the hole");
				// System.out.println("***************************************");
				doneSignal = new CountDownLatch(rules.size());
				cdlWriter = new CountDownLatch(rules.size());
				for (AbstractRule r : rules) {
					r.setDoneSignal(doneSignal);
					r.setFinished(false);
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			logger.debug("End of iteration - Latch : " + doneSignal.getCount());
			usableTriples.clear();
			usableTriples.addAll(newTriples);
			newTriples.clear();

			logger.debug("Usable triples: " + usableTriples.size());

			new_size = tripleStore.size();
			long step2Time = System.nanoTime();
			logger.debug((step2Time - stepTime) + "ns for "
					+ (new_size - old_size) + " triples");
			steps++;
			for (Triple triple : usableTriples.getAll()) {
				logger.trace(dictionnary.printTriple(triple));
			}
		} while (!usableTriples.isEmpty());

		//
		// Comparator comparator = new Comparator("jena_" + input);
		//
		// long size = comparator.compare(tripleStore, dictionnary);
		//
		// long endTime = System.nanoTime();
		// System.out.println("Dictionary size: "+dictionnary.size());
		// System.out.println("Initial triples: "+beginNbTriples);
		// System.out.println("Triples after inference: "+tripleStore.size());
		// System.out.println("Generated triples: "
		// + (tripleStore.size() - beginNbTriples));
		System.out.println((tripleStore.size() - beginNbTriples));
		logger.debug("Generated triples: "
				+ (tripleStore.size() - beginNbTriples));
		// System.out.println("Iterations: "+steps);
		// System.out.println("Parsing: "+(parsingTime-startTime)/1000000.0+"ns");
		// System.out.println("Inference: "+(endTime-parsingTime)/1000000.0+"ns");
		// System.out.println("Total time: "+(endTime-startTime)/1000000.0+"ns");
		// System.out.print("File writing: ");
		tripleStore.writeToFile("Inferred"
				+ (tripleStore.size() - beginNbTriples) + input + ".out",
				dictionnary);
		// System.out.println("ok");

		// String[] headers =
		// {"File","Size","i","Initial triples","Infered triples","Loops","Time","Correctness"};
		// String[] datas = { input, "" + i, "" + beginNbTriples,
		// "" + (tripleStore.size() - beginNbTriples), "" + steps,
		// "" + (endTime - parsingTime), "" + size };
		// writer.writeNext(datas);

	}

	static void shutdownAndAwaitTermination(ExecutorService pool) {
		// System.out.println("finishing");
		System.exit(-1);
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(50, TimeUnit.MILLISECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(50, TimeUnit.MILLISECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			System.out.println("piece of");
			pool.shutdownNow();

			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
