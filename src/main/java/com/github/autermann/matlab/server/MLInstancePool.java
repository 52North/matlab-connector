/*
 * Copyright (C) 2012-2013 by it's authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.autermann.matlab.server;

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
