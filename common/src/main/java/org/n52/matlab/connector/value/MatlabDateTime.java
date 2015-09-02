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


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabDateTime extends MatlabValue {
    private final DateTime time;

    public MatlabDateTime(DateTime time) {
        this.time = Preconditions.checkNotNull(time);
    }

    @Override
    public void accept(MatlabValueVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ReturningMatlabValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public MatlabType getType() {
        return MatlabType.DATE_TIME;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MatlabDateTime) {
            MatlabDateTime that = (MatlabDateTime) o;
            return this.value().equals(that.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    public DateTime value() {
        return time;
    }

    public double[] toArray() {
        DateTime utc = time.toDateTime(DateTimeZone.UTC);
        return new double[] {
            (double) utc.getYear(),
            (double) utc.getMonthOfYear(),
            (double) utc.getDayOfMonth(),
            (double) utc.getHourOfDay(),
            (double) utc.getMinuteOfHour(),
            (double) utc.getSecondOfMinute()
            + (double) utc.getMillisOfSecond() / 1000, };

    }
}
