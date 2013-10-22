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
package com.github.autermann.matlab.socket.ssl;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public interface SSLConstants {
    String KEYSTORE_TYPE_JKS = "JKS";
    String TRUST_MANAGER_ALGORITHM_PKIX = "PKIX";
    String KEY_MANAGER_ALGORITHM_SUN_X509 = "SunX509";
    String KEY_MANAGER_PROVIDER_SUN_JSSE = "SunJSSE";
    String PROTOCOL_TLS_V1 = "TLSv1";
    String KEY_ALGORITHM_RSA = "RSA";
    String CERTIFICATE_TYPE_X509 = "X.509";
}
