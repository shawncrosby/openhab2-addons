/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

public class WinkAuthenticationService {
    private static IWinkAuthenticationService instance;

    public static IWinkAuthenticationService getInstance() {
        if (instance == null) {
            instance = new CloudOauthWinkAuthenticationService();
        }
        return instance;
    }
}
