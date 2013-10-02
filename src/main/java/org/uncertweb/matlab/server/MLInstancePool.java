package org.uncertweb.matlab.server;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MLInstancePool {
    private static final Logger log = LoggerFactory
            .getLogger(MLInstancePool.class);
    private GenericObjectPool<MLInstance> pool;

    public MLInstancePool(MLInstancePoolConfig config) throws MLConnectorException {
        final InstanceFactory factory = new InstanceFactory(config.getInstanceConfig());
        this.pool = new GenericObjectPool<MLInstance>(factory);
        this.pool.setMaxActive(config.getNumThreads());
        this.pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    }

    public MLInstance getInstance() {
        try {
            return pool.borrowObject();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to borrow instance from pool" 
                                       + ex .toString(), ex);
        }
    }

    public void destroy() {
        try {
            pool.close();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to close pool" + ex.toString(), ex);
        }
    }

    public void returnInstance(MLInstance obj) {
        try {
            pool.returnObject(obj);
        } catch (Exception ex) {
            log.error("Unable to return instance to pool" + ex.toString(), ex);
        }
    }

    private class InstanceFactory extends BasePoolableObjectFactory<MLInstance> {
        private final MLInstanceConfig config;

        InstanceFactory(MLInstanceConfig config) {
            this.config = config;
        }

        @Override
        public MLInstance makeObject() throws Exception {
            return new MLInstance(config);
        }

        @Override
        public void destroyObject(MLInstance instance) throws Exception {
            instance.destroy();
        }
    }
}
