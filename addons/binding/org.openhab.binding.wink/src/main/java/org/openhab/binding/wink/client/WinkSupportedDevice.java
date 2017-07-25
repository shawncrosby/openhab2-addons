/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

public enum WinkSupportedDevice {
    DIMMABLE_LIGHT("light_bulb", "/light_bulbs"),
    BINARY_SWITCH("binary_switch", "/binary_switches"),
    REMOTE("remote", "/remotes"),
    LOCK("lock", "/locks"),
    HUB("hub", "/hubs");

    private String device_type;
    private String path;

    WinkSupportedDevice(String device_type, String path) {
        this.device_type = device_type;
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getDeviceType() {
        return this.device_type;
    }

    public static WinkSupportedDevice lookup(String device_type) {
        for (WinkSupportedDevice dType : values()) {
            if (dType.getDeviceType().equals(device_type)) {
                return dType;
            }
        }
        return null;
    }
}
