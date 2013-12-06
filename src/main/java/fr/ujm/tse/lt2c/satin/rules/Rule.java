package fr.ujm.tse.lt2c.satin.rules;

import java.util.concurrent.ExecutorService;

import fr.ujm.tse.lt2c.satin.buffer.TripleDistributor;
import fr.ujm.tse.lt2c.satin.interfaces.BufferListener;
import fr.ujm.tse.lt2c.satin.interfaces.TripleBuffer;
import fr.ujm.tse.lt2c.satin.rules.run.AbstractRun;


public class Rule implements BufferListener{
	
	/**
	 * The Buffer receives the triples, notify the the object when it's full
	 * The object launch a new Run with the received triples 
	 * The distributor sends the new triples to the subscribers
	 */
	
	TripleBuffer tripleBuffer;
	AbstractRun ruleRun;
//	TripleDistributor distributor;

	ExecutorService executor;

	public Rule(AbstractRun ruleRun, ExecutorService executor) {
		super();
		this.tripleBuffer = ruleRun.getTripleBuffer();
		this.ruleRun = ruleRun;
		this.executor = executor;
//		this.distributor = new TripleDistributor();this.distributor.setName(this.ruleRun.getRuleName()+"Dis");
		
		this.tripleBuffer.addBufferListener(this);
		
	}
	
	@Override
	public void bufferFull() {
//		System.out.println("Full "+this.ruleRun+" "+this.executor);
		this.executor.submit(this.ruleRun);
		//How to pass usable triples to already instantiate rule without corrupt each running threads
		// => No more a problem with buffers
	}
	
	public long[] getInputMatchers(){
		return this.ruleRun.getInputMatchers();
	}
	
	public long[] getOutputMatchers(){
		return this.ruleRun.getOutputMatchers();
	}
	
	public TripleBuffer getTripleBuffer(){
		return this.tripleBuffer;
	}

	public TripleDistributor getTripleDistributor() {
		return this.ruleRun.getDistributor();
	}
	
	public String name(){
		return this.ruleRun.getRuleName();
	}

	public AbstractRun getRun() {
		return this.ruleRun;
	}
}
