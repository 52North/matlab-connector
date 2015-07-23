/*
 * Copyright (C) 2012-2015 by it's authors.
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabInstancePoolConfiguration {
    private final int numThreads;
    private final MatlabInstanceConfiguration instanceConfig;

    private MatlabInstancePoolConfiguration(int numThreads,
                                            MatlabInstanceConfiguration instanceConfig) {
        this.numThreads = numThreads;
        this.instanceConfig = instanceConfig == null
                              ? MatlabInstanceConfiguration
                .builder().build() : instanceConfig;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public MatlabInstanceConfiguration getInstanceConfig() {
        return instanceConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int numThreads = 1;
        private MatlabInstanceConfiguration instanceConfig;

        public Builder withMaximalNumInstances(int numThreads) {
            checkArgument(numThreads >= 0);
            this.numThreads = numThreads;
            return this;
        }

        public Builder withInstanceConfig(
                MatlabInstanceConfiguration instanceConfig) {
            this.instanceConfig = checkNotNull(instanceConfig);
            return this;
        }

        public MatlabInstancePoolConfiguration build() {
            return new MatlabInstancePoolConfiguration(numThreads, instanceConfig);
        }
    }
}
