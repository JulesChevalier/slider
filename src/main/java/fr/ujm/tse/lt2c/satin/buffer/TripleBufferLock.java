package fr.ujm.tse.lt2c.satin.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.ujm.tse.lt2c.satin.interfaces.BufferListener;
import fr.ujm.tse.lt2c.satin.interfaces.Triple;
import fr.ujm.tse.lt2c.satin.interfaces.TripleBuffer;
import fr.ujm.tse.lt2c.satin.interfaces.TripleStore;
import fr.ujm.tse.lt2c.satin.triplestore.VerticalPartioningTripleStoreRWLock;

public class TripleBufferLock implements TripleBuffer {

	TripleStore buffer1;
	TripleStore buffer2;
	ReentrantReadWriteLock rwlock1 = new ReentrantReadWriteLock();
	ReentrantReadWriteLock rwlock2 = new ReentrantReadWriteLock();
	Collection<BufferListener> bufferListeners;

	static final long BUFFER_SIZE = 500;

	public TripleBufferLock() {
		this.buffer1 = new VerticalPartioningTripleStoreRWLock();
		// this.buffer1_size = 0;
		this.buffer2 = new VerticalPartioningTripleStoreRWLock();
		// this.buffer2_size = 0;
		this.bufferListeners = new ArrayList<>();
	}

	@Override
	public void add(Triple triple) {
		rwlock1.writeLock().lock();
		try {
			this.buffer1.add(triple);
			if (this.buffer1.size() >= BUFFER_SIZE) {
//				System.out.println("Buffer full");
				if (this.buffer2.isEmpty()) {
//					System.out.println("Buffer switch");
					TripleStore temp = buffer1;
					buffer1 = buffer2;
					buffer2 = temp;
					for (BufferListener bufferListener : bufferListeners) {
						bufferListener.bufferFull();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock1.writeLock().unlock();
		}
	}

	@Override
	public TripleStore clear() {
		TripleStore dump = null;
		rwlock2.writeLock().lock();
		try {
			dump = buffer2;
			buffer2 = new VerticalPartioningTripleStoreRWLock();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock2.writeLock().unlock();
		}
		return dump;
	}

	@Override
	public void addBufferListener(BufferListener bufferListener) {
		this.bufferListeners.add(bufferListener);
	}

	@Override
	public TripleStore close() {
		return this.buffer1;
	}

}
