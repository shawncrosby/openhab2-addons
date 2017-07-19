/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

import java.util.List;
import java.util.Map;

public class LogOnlyWinkClient implements IWinkClient {

    @Override
    public List<IWinkDevice> listDevices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IWinkDevice getDevice(WinkSupportedDevice type, String Id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IWinkDevice updateDeviceState(IWinkDevice device, Map<String, String> updatedState) {
        // TODO Auto-generated method stub
        return null;
    }

}
