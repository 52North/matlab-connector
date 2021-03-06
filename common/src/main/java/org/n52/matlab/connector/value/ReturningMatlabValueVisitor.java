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

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface ReturningMatlabValueVisitor<T> {

    T visit(MatlabArray array);

    T visit(MatlabBoolean bool);

    T visit(MatlabCell cell);

    T visit(MatlabMatrix matrix);

    T visit(MatlabScalar scalar);

    T visit(MatlabString string);

    T visit(MatlabStruct struct);

    T visit(MatlabFile file);

    T visit(MatlabDateTime time);
}
