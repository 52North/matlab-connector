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
package com.github.autermann.matlab;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public interface MatlabEncoding {

    /**
     * Parses a {@link MatlabRequest} from an {@link InputStream}.
     *
     * @param is the <code>InputStream</code> to parse from
     *
     * @return the parsed <code>MatlabRequest</code>
     */
    MatlabRequest decodeRequest(InputStream is);

    /**
     * Parses a {@link MatlabRequest} from an {@link Reader}.
     *
     * @param is the <code>Reader</code> to parse from
     *
     * @return the parsed <code>MatlabRequest</code>
     */
    MatlabRequest decodeRequest(Reader is);

    /**
     * Parses a {@link MatlabRequest} from an {@link String}.
     *
     * @param request the <code>String</code> to parse from
     *
     * @return the parsed <code>MatlabRequest</code>
     */
    MatlabRequest decodeRequest(String request);

    /**
     * Parses a {@link MatlabResponse} from an {@link InputStream}.
     *
     * @param is the <code>InputStream</code> to parse from
     *
     * @return the parsed <code>MatlabResponse</code>
     */
    MatlabResponse decodeResponse(InputStream is);

    /**
     * Parses a {@link MatlabResponse} from an {@link Reader}.
     *
     * @param is the <code>Reader</code> to parse from
     *
     * @return the parsed <code>MatlabResponse</code>
     */
    MatlabResponse decodeResponse(Reader is);

    /**
     * Parses a {@link MatlabResponse} from an {@link String}.
     *
     * @param response the <code>String</code> to parse from
     *
     * @return the parsed <code>MatlabResponse</code>
     */
    MatlabResponse decodeResponse(String response);

    /**
     * Outputs a {@link MatlabRequest} to an {@link OutputStream}.
     *
     * @param request the <code>MatlabRequest</code> to output
     * @param os      the <code>OutputStream</code> to output to
     */
    void encodeRequest(MatlabRequest request, OutputStream os);

    /**
     * Outputs a {@link MatlabRequest} to an {@link Writer}.
     *
     * @param request the <code>MatlabRequest</code> to output
     * @param os      the <code>Writer</code> to output to
     */
    void encodeRequest(MatlabRequest request, Writer os);

    /**
     * Outputs a {@link MatlabRequest} to an {@link OutputStream}.
     *
     * @param request the <code>MatlabRequest</code> to output
     *
     * @return the encoded <code>MatlabRequest</code>
     */
    String encodeRequest(MatlabRequest request);

    /**
     * Outputs a {@link MatlabResponse} to an {@link OutputStream}.
     *
     * @param response the <code>MatlabResponse</code> to output
     * @param os       the <code>OutputStream</code> to output to
     */
    void encodeResponse(MatlabResponse response, OutputStream os);

    /**
     * Outputs a {@link MatlabResponse} to an {@link Writer}.
     *
     * @param response the <code>MatlabResponse</code> to output
     * @param os       the <code>Writer</code> to output to
     */
    void encodeResponse(MatlabResponse response, Writer os);

    /**
     * Outputs a {@link MatlabResponse} to an {@link OutputStream}.
     *
     * @param response the <code>MatlabResponse</code> to output
     *
     * @return the encoded <code>MatlabResponse</code>
     */
    String encodeResponse(MatlabResponse response);

}
