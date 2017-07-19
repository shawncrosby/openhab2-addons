/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

public class WinkClient {
    private static IWinkClient instance;

    public static IWinkClient getInstance() {
        if (instance == null) {
            instance = new CloudRestfulWinkClient();
        }

        return instance;
    }

    public static void setInstance(IWinkClient testClient) {
        instance = testClient;
    }
}
