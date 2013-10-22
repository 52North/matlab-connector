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
