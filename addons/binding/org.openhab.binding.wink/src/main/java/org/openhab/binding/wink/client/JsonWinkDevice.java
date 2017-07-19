/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class JsonWinkDevice implements IWinkDevice {
    private JsonObject json;

    public JsonWinkDevice(JsonObject element) {
        this.json = element;
    }

    @Override
    public String getId() {
        return json.get("uuid").getAsString();
    }

    @Override
    public String getName() {
        return json.get("name").getAsString();
    }

    @Override
    public WinkSupportedDevice getDeviceType() {
        return WinkSupportedDevice.lookup(json.get("object_type").getAsString());
    }

    @Override
    public String getPubNubSubscriberKey() {
        return json.get("subscription").getAsJsonObject().get("pubnub").getAsJsonObject().get("subscribe_key")
                .getAsString();
    }

    @Override
    public String getPubNubChannel() {
        return json.get("subscription").getAsJsonObject().get("pubnub").getAsJsonObject().get("channel").getAsString();
    }

    @Override
    public String getProperty(String property) {
        return json.get(property).getAsString();
    }

    @Override
    public Map<String, String> getCurrentState() {
        JsonObject data = json.get("last_reading").getAsJsonObject();
        return toMap(data);
    }

    @Override
    public Map<String, String> getDesiredState() {
        JsonObject data = json.get("desired_state").getAsJsonObject();
        return toMap(data);
    }

    private Map<String, String> toMap(JsonObject json) {
        return new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {
        }.getType());
    }

}
