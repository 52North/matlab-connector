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
package com.github.autermann.matlab.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabClientConfiguration {
    private final URI address;

    private MatlabClientConfiguration(URI address) {
        this.address = address;
    }

    public URI getAddress() {
        return address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URI address;

        private Builder() {
        }

        public Builder withAddress(URI address) {
            checkNotNull(address);
            checkArgument(address.getScheme().equals("ws") ||
                          address.getScheme().equals("wss"));
            this.address = address;
            return this;
        }

        public Builder withAddress(String host, int port) {
            checkNotNull(host);
            checkArgument(port > 0);
            return withAddress(URI.create(String.format("ws://%s:%s", host, port)));
        }

        public MatlabClientConfiguration build() {
            checkState(address != null);
            return new MatlabClientConfiguration(address);
        }
    }
}
