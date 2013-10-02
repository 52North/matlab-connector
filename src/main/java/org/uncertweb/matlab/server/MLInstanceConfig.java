package org.uncertweb.matlab.server;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLInstanceConfig {
    private final String baseDir;

    private MLInstanceConfig(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseDir;

        public Builder withBaseDir(String baseDir) {
            Preconditions.checkNotNull(baseDir);
            this.baseDir = baseDir;
            return this;
        }

        public MLInstanceConfig build() {
            return new MLInstanceConfig(baseDir);
        }
    }
}
