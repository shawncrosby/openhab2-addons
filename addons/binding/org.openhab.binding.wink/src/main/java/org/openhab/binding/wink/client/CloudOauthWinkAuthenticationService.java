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
import java.util.prefs.Preferences;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This WinkAuthenticationService requires that the user apply for and receive their own
 * wink app id and appropriate client secret. It Also requires that they must perform
 * a little authentication and get their initial access token and refresh token. Initial access
 * token can be fudged and the client will actually perform and persist a refresh as necessary.
 *
 * In order to work, you must put a wink.cfg file in your $openhab/conf/services folder with the
 * following parameters defined
 *
 * ---wink.cfg---
 * auth_token=initia_auth_token_xsdfljkwelfj
 * client_id=wink_app_client_id_lakjdsflakjdf
 * client_secret=wink_app_client_secret_laskdjflakjsdf
 * refresh_token=refresh_token_from_external_auth_call_aldjflakjsdoie
 * ---end---
 *
 * @author scrosby
 *
 */
public class CloudOauthWinkAuthenticationService implements IWinkAuthenticationService {
    private static final String WINK_NODE = "org.openhab.wink";
    private static final String ACCESS_TOKEN = "auth_token";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REFRESH_TOKEN = "refresh_token";

    private String token;

    public CloudOauthWinkAuthenticationService() {
        token = Preferences.userRoot().node(WINK_NODE).get(ACCESS_TOKEN, null);
    }

    @Override
    public String getAuthToken() {
        return getCurrentToken();
    }

    @Override
    public String refreshToken() {
        // get clientid, secret an refresh token
        Preferences prefs = Preferences.userRoot().node(WINK_NODE);
        String clientId = prefs.get(CLIENT_ID, null);
        String clientSecret = prefs.get(CLIENT_SECRET, null);
        String refresh = prefs.get(REFRESH_TOKEN, null);

        Client winkClient = ClientBuilder.newClient();
        WebTarget target = winkClient.target("https://api.wink.com");
        WebTarget newToken = target.path("/oauth2/token");

        Map<String, String> payload = new HashMap<String, String>();
        payload.put("client_id", clientId);
        payload.put("client_secret", clientSecret);
        payload.put("grant_type", "refresh_token");
        payload.put("refresh_token", refresh);

        String json = new Gson().toJson(payload);

        Response response = newToken.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(json));
        JsonObject responseJson = getResultAsJson(response);

        String newAccessToken = responseJson.get("data").getAsJsonObject().get(ACCESS_TOKEN).getAsString();
        // get and store new token
        prefs.put(ACCESS_TOKEN, newAccessToken);
        token = newAccessToken;

        return token;
    }

    private JsonObject getResultAsJson(Response response) {
        String result = response.readEntity(String.class);
        JsonParser parser = new JsonParser();
        JsonObject resultJson = parser.parse(result).getAsJsonObject();
        return resultJson;
    }

    private String getCurrentToken() {
        return token;
    }

}
