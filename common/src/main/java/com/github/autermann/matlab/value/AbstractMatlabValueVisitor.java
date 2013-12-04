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


public class AbstractMatlabValueVisitor implements MatlabValueVisitor {

    @Override
    public void visit(MatlabArray array) {
    }

    @Override
    public void visit(MatlabBoolean bool) {
    }

    @Override
    public void visit(MatlabCell cell) {
    }

    @Override
    public void visit(MatlabMatrix matrix) {
    }

    @Override
    public void visit(MatlabScalar scalar) {
    }

    @Override
    public void visit(MatlabString string) {
    }

    @Override
    public void visit(MatlabStruct struct) {
    }

    @Override
    public void visit(MatlabFile file) {
    }

    @Override
    public void visit(MatlabDateTime time) {
    }

}
