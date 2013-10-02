package org.uncertweb.matlab.server;

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
