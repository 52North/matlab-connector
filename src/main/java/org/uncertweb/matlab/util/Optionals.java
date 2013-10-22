/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.uncertweb.matlab.util;

import java.util.Arrays;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class Optionals {

    private  Optionals() {
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
