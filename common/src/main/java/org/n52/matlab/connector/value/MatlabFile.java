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
package org.n52.matlab.connector.value;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabFile extends MatlabValue {
    private Path file;
    private byte[] content;

    public MatlabFile(Path file) {
        this(Objects.requireNonNull(file), null);
    }

    public MatlabFile(byte[] content) {
        this(null, Objects.requireNonNull(content));
    }

    private MatlabFile(Path file, byte[] content) {
        this.file = file;
        this.content = content;
    }

    public boolean isLoaded() {
        return content != null;
    }

    public boolean isSaved() {
        return file != null;
    }

    public MatlabFile load()
            throws IOException {
        checkState(isSaved());
        this.content = Files.readAllBytes(file);
        return this;
    }

    public MatlabFile unload() {
        checkState(isSaved());
        this.content = null;
        return this;
    }

    public MatlabFile save(Path file)
            throws IOException {
        Objects.requireNonNull(file);
        checkState(isLoaded());
        Files.write(file, content);
        this.file = file;
        return this;
    }

    public MatlabFile delete()
            throws IOException {
        if (file != null) {
            Files.delete(file);
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

    public Path getFile() {
        return file;
    }

    public byte[] getContent()
            throws IOException {
        if (!isLoaded()) {
            load();
        }
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabFile) {
            MatlabFile that = (MatlabFile) o;
            try {
                return Arrays.equals(this.getContent(), that.getContent());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        return false;
    }

    @Override
    public int hashCode() {
        try {
            return Objects.hash(getFile(), getContent());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        if (isLoaded()) {
            return String.format("%s[length=%s]",
                                 getClass().getSimpleName(),
                                 this.content.length);
        } else {
            return String.format("%s[location=%s]",
                                 getClass().getSimpleName(),
                                 this.file.toAbsolutePath());
        }
    }

    public static MatlabFile load(Path file)
            throws IOException {
        return new MatlabFile(file).load();
    }

    public static MatlabFile save(byte[] content, Path file)
            throws IOException {
        return new MatlabFile(content).save(file);
    }

}
