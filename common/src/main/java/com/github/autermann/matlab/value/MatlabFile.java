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
package com.github.autermann.matlab.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabFile extends MatlabValue {
    private File file;
    private byte[] content;

    public MatlabFile(File file) {
        this(checkNotNull(file), null);
    }

    public MatlabFile(byte[] content) {
        this(null, checkNotNull(content));
    }

    private MatlabFile(File file, byte[] content) {
        this.file = file;
        this.content = content;
    }

    public boolean isLoaded() {
        return content != null;
    }

    public boolean isSaved() {
        return file != null;
    }

    public MatlabFile load() throws IOException {
        checkState(isSaved());
        this.content = ByteStreams.toByteArray(Files
                .newInputStreamSupplier(file));
        return this;
    }

    public MatlabFile unload() {
        checkState(isSaved());
        this.content = null;
        return this;
    }

    public MatlabFile save(File file) throws IOException {
        checkNotNull(file);
        checkState(isLoaded());
        ByteStreams.write(content, Files.newOutputStreamSupplier(file));
        this.file = file;
        return this;
    }

    public MatlabFile delete() {
        if (file != null) {
            file.delete();
            file = null;
        }
        return this;
    }

    @Override
    public void accept(MatlabValueVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ReturningMatlabValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public MatlabType getType() {
        return MatlabType.FILE;
    }

    public File getFile() {
        return file;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabFile) {
            MatlabFile that = (MatlabFile) o;
            try {
                if (!this.isLoaded()) {
                    this.load();
                }
                if (!that.isLoaded()) {
                    that.load();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return Arrays.equals(this.getContent(), that.getContent());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFile(), getContent());
    }

    public static MatlabFile load(File file) throws IOException {
        return new MatlabFile(file).load();
    }

    public static MatlabFile save(byte[] content, File file) throws IOException {
        return new MatlabFile(content).save(file);
    }

}
