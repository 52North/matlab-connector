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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
class MatlabInstancePoolDestroyer implements Runnable {
    private static final Logger log = LoggerFactory
            .getLogger(MatlabInstancePoolDestroyer.class);

    private final MatlabInstancePool pool;

    MatlabInstancePoolDestroyer(MatlabInstancePool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {
        log.info("Destroying MATLAB instance pool...");
        pool.destroy();
        log.info("Shutdown complete.");
    }

}
