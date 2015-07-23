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

import java.io.File;

import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabInstanceConfiguration {
    private final File baseDir;
    private final int port;
    private final boolean hidden;

    private MatlabInstanceConfiguration(File baseDir, int port, boolean hidden) {
        this.baseDir = baseDir;
        this.port = port;
        this.hidden = hidden;
    }

    public Optional<File> getBaseDir() {
        return Optional.fromNullable(this.baseDir);
    }

    public int getPort() {
        return this.port;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public static final int DEFAULT_PORT = 2100;
        private int port = DEFAULT_PORT;
        private File baseDir;
        private boolean hidden = false;

        public Builder withBaseDir(String baseDir) {
            return withBaseDir(new File(checkNotNull(baseDir)));
        }

        public Builder withBaseDir(File baseDir) {
            checkNotNull(baseDir);
            checkArgument(baseDir.exists());
            checkArgument(baseDir.isDirectory());
            checkArgument(baseDir.canRead());
            this.baseDir = baseDir;
            return this;
        }

        public Builder atPort(int port) {
            checkArgument(port > 0);
            this.port = port;
            return this;
        }

        public Builder hidden() {
            return hidden(true);
        }

        public Builder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public MatlabInstanceConfiguration build() {
            return new MatlabInstanceConfiguration(baseDir, port, hidden);
        }
    }
}
