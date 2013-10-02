package org.uncertweb.matlab.server;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MLInstancePool {

	private final Logger logger = LoggerFactory.getLogger(MLInstancePool.class);

	private List<MLInstance> free;
	private List<MLInstance> busy;

	public MLInstancePool(int numInstances) throws MLConnectorException {
		this(numInstances, null);
	}

	public MLInstancePool(int numInstances, String baseDir) throws MLConnectorException {
		// create arrays
		free = new ArrayList<MLInstance>();
		busy = new ArrayList<MLInstance>();

		// create instances
		logger.info("Creating {} instances for pool...", numInstances);
		for (int i = 0; i < numInstances; i++) {
			final MLInstance instance = new MLInstance(baseDir);
			free.add(instance);
			instance.addListener(new MLInstanceListener() {					
				@Override
				public void instanceFree() {
					busy.remove(instance);
					free.add(instance);
				}
			});
		}
		logger.info("Pool initialised!");
	}	

	public synchronized MLInstance getInstance() {
		MLInstance instance = null;
		while (instance == null) {
			if (free.size() > 0) {
				instance = free.remove(0);
				busy.add(instance);
			}
		}
		return instance;
	}

	public void destroy() {
		// aggressive, could wait until busy ones are finished?
		List<MLInstance> all = new ArrayList<MLInstance>(free);
		all.addAll(busy);
		for (MLInstance instance : all) {
			try {
				instance.destroy();
			}
			catch (MLConnectorException e) {
				logger.warn("Couldn't destroy instance.", e);
			}
		}
	}

}
