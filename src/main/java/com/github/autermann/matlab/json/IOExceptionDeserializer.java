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
package com.github.autermann.matlab.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * {@link MLProxyException} deserializer.
 *
 * @author Richard Jones
 *
 */
public class IOExceptionDeserializer implements JsonDeserializer<IOException> {

    @Override
    public IOException deserialize(JsonElement elem, Type type,
                                   JsonDeserializationContext ctx)
            throws JsonParseException {
        return new IOException(elem.getAsJsonObject()
                .get(JSONConstants.IOEXCEPTION)
                .getAsString());
    }
}
