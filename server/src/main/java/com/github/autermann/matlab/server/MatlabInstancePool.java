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

import com.github.autermann.matlab.MatlabException;

public class MatlabInstancePool {
    private static final Logger log = LoggerFactory
            .getLogger(MatlabInstancePool.class);
    private final GenericObjectPool<MatlabInstance> pool;

    public MatlabInstancePool(MatlabInstancePoolConfiguration config) throws
            MatlabException {
        final InstanceFactory factory = new InstanceFactory(config
                .getInstanceConfig());
        this.pool = new GenericObjectPool<MatlabInstance>(factory);
        this.pool.setMaxActive(config.getNumThreads());
        this.pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
    }

    public MatlabInstance getInstance() {
        try {
            return pool.borrowObject();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to borrow instance from pool" +
                                       ex.toString(), ex);
        }
    }

    public void destroy() {
        try {
            pool.close();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to close pool" + ex.toString(), ex);
        }
    }

    public void returnInstance(MatlabInstance obj) {
        try {
            pool.returnObject(obj);
        } catch (Exception ex) {
            log.error("Unable to return instance to pool" + ex.toString(), ex);
        }
    }

    private class InstanceFactory extends BasePoolableObjectFactory<MatlabInstance> {
        private final MatlabInstanceConfiguration config;

        InstanceFactory(MatlabInstanceConfiguration config) {
            this.config = config;
        }

        @Override
        public MatlabInstance makeObject() throws Exception {
            return new MatlabInstance(config);
        }

        @Override
        public void destroyObject(MatlabInstance instance) throws Exception {
            instance.destroy();
        }
    }
}
