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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class NamedAndGroupedThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String nameFormat;
    private final boolean daemon;
    private final int priority;
    private final int stackSize;
    private final UncaughtExceptionHandler eh;

    private NamedAndGroupedThreadFactory(ThreadGroup group, String nameFormat,
                                         boolean daemon, int priority,
                                         int stackSize,
                                         UncaughtExceptionHandler eh) {
        this.daemon = daemon;
        this.group = Preconditions.checkNotNull(group);
        this.nameFormat = Preconditions.checkNotNull(nameFormat);
        Preconditions.checkArgument(priority >= Thread.MIN_PRIORITY ||
                                    priority <= Thread.MAX_PRIORITY, "invalid priority: %d", priority);
        this.priority = priority;
        Preconditions
                .checkArgument(stackSize >= 0, "invalid stacksize: %d", stackSize);
        this.stackSize = stackSize;
        this.eh = eh;
    }

    @Override
    public Thread newThread(Runnable r) {
        final String name = String.format(nameFormat, threadNumber
                .getAndIncrement());
        final Thread t = new Thread(group, r, name, stackSize);
        t.setDaemon(daemon);
        t.setPriority(priority);
        if (eh != null) {
            t.setUncaughtExceptionHandler(eh);
        }
        return t;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private String name;
        private boolean daemon = false;
        private int priority = Thread.NORM_PRIORITY;
        private int stackSize = 0;
        private UncaughtExceptionHandler exceptionHandler = null;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = Preconditions.checkNotNull(name);
            return this;
        }

        public Builder priority(int priority) {
            Preconditions.checkArgument(priority >= Thread.MIN_PRIORITY ||
                                        priority <= Thread.MAX_PRIORITY);
            this.priority = priority;
            return this;
        }

        public Builder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public Builder stackSize(int stackSize) {
            Preconditions
                    .checkArgument(stackSize >= 0, "invalid stacksize: %d", stackSize);
            this.stackSize = stackSize;
            return this;
        }

        public Builder exceptionHandler(UncaughtExceptionHandler eh) {
            this.exceptionHandler = Preconditions.checkNotNull(eh);
            return this;
        }

        public ThreadFactory build() {
            if (name == null) {
                name = "pool-" + poolNumber.getAndIncrement();
            }
            final ThreadGroup group = new ThreadGroup(name);
            final String nameFormat = name + "-%d";
            return new NamedAndGroupedThreadFactory(group, nameFormat, daemon, priority, stackSize, exceptionHandler);
        }
    }
}
