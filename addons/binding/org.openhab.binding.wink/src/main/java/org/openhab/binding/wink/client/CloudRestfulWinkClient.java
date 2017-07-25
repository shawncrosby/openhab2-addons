/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.wink.client;

import static org.openhab.binding.wink.WinkBindingConstants.WINK_URI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CloudRestfulWinkClient implements IWinkClient {

    private static final Logger log = LoggerFactory.getLogger(CloudRestfulWinkClient.class);

    private Client winkClient = ClientBuilder.newBuilder().build();
    private WebTarget winkTarget = winkClient.target(WINK_URI);

    @Override
    public List<IWinkDevice> listDevices() {
        log.debug("Getting all devices for user");
        List<IWinkDevice> ret = new ArrayList<IWinkDevice>();

        WebTarget target = winkTarget.path("/users/me/wink_devices");
        JsonArray resultJson = executeGet(target).getAsJsonArray();
        Iterator<JsonElement> iterator = resultJson.getAsJsonArray().iterator();

        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (!element.isJsonObject()) {
                continue;
            }
            ret.add(new JsonWinkDevice(element.getAsJsonObject()));
        }
        return ret;
    }

    @Override
    public IWinkDevice getDevice(WinkSupportedDevice type, String Id) {
        log.debug("Getting Device: {}", Id);
        WebTarget target = winkTarget.path(type.getPath() + "/" + Id);
        JsonObject resultJson = executeGet(target).getAsJsonObject();

        return new JsonWinkDevice(resultJson);
    }

    @Override
    public IWinkDevice updateDeviceState(IWinkDevice device, Map<String, String> updatedState) {
        WebTarget target = winkTarget.path(device.getDeviceType().getPath() + "/" + device.getId());
        String desired_state = new Gson().toJson(updatedState);
        Response response = executePut(target, desired_state);
        JsonObject jsonResult = getResultAsJson(response).getAsJsonObject();

        return new JsonWinkDevice(jsonResult);
    }

    private Response executePut(WebTarget target, String payload) {
        String token = WinkAuthenticationService.getInstance().getAuthToken();

        Response response = doPut(target, payload, token);
        if (response.getStatus() != 200) {
            log.debug("Got status {}, retrying with new token", response.getStatus());
            token = WinkAuthenticationService.getInstance().refreshToken();
            response = doPut(target, payload, token);
        }

        return response;
    }

    private JsonElement executeGet(WebTarget target) {
        String token = WinkAuthenticationService.getInstance().getAuthToken();
        Response response = doGet(target, token);
        if (response.getStatus() != 200) {
            log.debug("Got status {}, retrying with new token", response.getStatus());
            token = WinkAuthenticationService.getInstance().refreshToken();
            response = doGet(target, token);
        }
        JsonElement resultJson = getResultAsJson(response);
        return resultJson;
    }

    private Response doGet(WebTarget target, String token) {
        log.debug("Doing Get: {}", target);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token)
                .get();
        return response;
    }

    private Response doPut(WebTarget target, String payload, String token) {
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + token)
                .put(Entity.json(payload));
        return response;
    }

    private JsonElement getResultAsJson(Response response) {
        String result = response.readEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonObject resultJson = parser.parse(result).getAsJsonObject();

        JsonElement ret = resultJson.get("data");

        return ret;
    }

}
