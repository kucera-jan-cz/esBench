package org.esbench.elastic.sender.cluster;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ClientListener;

/**
 * Notify other thread about client shutdown using given lock and condition signaling.  
 */
class ClientShutdownListener implements ClientListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientShutdownListener.class);
	private final Condition notEmpty;
	private final Lock lock;

	public ClientShutdownListener(Lock lock, Condition notEmpty) {
		this.lock = lock;
		this.notEmpty = notEmpty;
	}

	@Override
	public void clientConnected(com.hazelcast.core.Client client) {
	}

	@Override
	public void clientDisconnected(com.hazelcast.core.Client client) {
		LOGGER.debug("Client {} disconnected", client.getUuid());
		lock.lock();
		try {
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

}
