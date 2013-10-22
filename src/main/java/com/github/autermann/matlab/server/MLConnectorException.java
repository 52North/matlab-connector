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

public class MLConnectorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>MLConnectorException</code> instance with the given
     * message.
     *
     * @param message the exception message
     */
    public MLConnectorException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>MLConnectorException</code> instance with the given
     * message and cause.
     *
     * @param message the exception message
     * @param cause   the cause of the exception
     */
    public MLConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

}
