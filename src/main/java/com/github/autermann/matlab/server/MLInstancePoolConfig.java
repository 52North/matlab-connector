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

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLInstancePoolConfig {
    private final int numThreads;
    private final MLInstanceConfig instanceConfig;

    private MLInstancePoolConfig(int numThreads,
                                 MLInstanceConfig instanceConfig) {
        this.numThreads = numThreads;
        this.instanceConfig = instanceConfig == null ? MLInstanceConfig
                .builder().build() : instanceConfig;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public static Builder builder() {
        return new Builder();
    }

    public MLInstanceConfig getInstanceConfig() {
        return instanceConfig;
    }

    public static class Builder {
        private int numThreads;
        private MLInstanceConfig instanceConfig;

        public Builder withMaximalNumInstances(int numThreads) {
            Preconditions.checkArgument(numThreads >= 0);
            this.numThreads = numThreads;
            return this;
        }

        public Builder withInstanceConfig(MLInstanceConfig instanceConfig) {
            this.instanceConfig = Preconditions.checkNotNull(instanceConfig);
            return this;
        }

        public MLInstancePoolConfig build() {
            return new MLInstancePoolConfig(numThreads, instanceConfig);
        }
    }
}
