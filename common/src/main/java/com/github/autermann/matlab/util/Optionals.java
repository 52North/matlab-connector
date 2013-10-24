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
package com.github.autermann.matlab.util;

import java.util.Arrays;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class Optionals {

    private Optionals() {
    }

    public static <T> Predicate<Optional<? extends T>> isPresent() {
        return IsPresent.instance().withNarrowedType();
    }

    public static <T> boolean any(Optional<? extends T>... optionals) {
        return Iterables.any(Arrays.asList(optionals), isPresent());
    }

    public static <T> boolean all(Optional<? extends T>... optionals) {
        return Iterables.all(Arrays.asList(optionals), isPresent());
    }

    private static class IsPresent implements Predicate<Optional<?>> {
        private static final IsPresent INSTANCE = new IsPresent();

        private IsPresent() {
        }

        @Override
        public boolean apply(
                Optional<?> input) {
            return input.isPresent();
        }

        @SuppressWarnings(value = "unchecked")
        public <T> Predicate<Optional<? extends T>> withNarrowedType() {
            return (Predicate<Optional<? extends T>>) this;
        }

        public static IsPresent instance() {
            return INSTANCE;
        }
    }

}
