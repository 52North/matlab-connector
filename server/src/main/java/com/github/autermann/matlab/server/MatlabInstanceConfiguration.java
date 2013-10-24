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
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabInstanceConfiguration {
    private final String baseDir;

    private MatlabInstanceConfiguration(String baseDir) {
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

        public MatlabInstanceConfiguration build() {
            return new MatlabInstanceConfiguration(baseDir);
        }
    }
}
