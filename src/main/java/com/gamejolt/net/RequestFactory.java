/**
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.gamejolt.net;

import java.util.LinkedHashMap;
import java.util.Map;


public class RequestFactory {
    private static final String BASE_URL = "http://gamejolt.com/api/game/";
    private static final String DEFAULT_VERSION = "1";

    private int gameId;
    private String privateKey;
    private SignatureFactory signatureFactory;
    private String version = DEFAULT_VERSION;

    public RequestFactory(int gameId, String privateKey) {
        this(gameId, privateKey, new SignatureFactory());
    }

    protected RequestFactory(int gameId, String privateKey, SignatureFactory signatureFactory) {
        this.gameId = gameId;
        this.privateKey = privateKey;
        this.signatureFactory = signatureFactory;
    }

    public HttpRequest buildVerifyUserRequest(String username, String userToken) {
        String baseUrl = createUrl("users/auth/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);

        Map<String, String> signatureParameters = createParameterMap(parameters);
        signatureParameters.put("user_token", userToken + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildAchievedTrophyRequest(String username, String userToken, String trophyId) {
        String baseUrl = createUrl("trophies/add-achieved");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);
        parameters.put("trophy_id", trophyId);

        Map<String, String> signatureParameters = createParameterMap(parameters);
        signatureParameters.put("user_token", userToken + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildTrophyRequest(String username, String userToken, String trophyId) {
        String baseUrl = createUrl("trophies/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);
        parameters.put("trophy_id", trophyId);

        Map<String, String> signatureParameters = createParameterMap(parameters);
        signatureParameters.put("user_token", userToken + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildTrophiesRequest(String username, String userToken, String achieved) {
        String baseUrl = createUrl("trophies/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);
        parameters.put("achieved", achieved);

        Map<String, String> signatureParameters = createParameterMap(parameters);
        signatureParameters.put("user_token", userToken + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HttpRequest buildStoreGameDataRequest(String name, String data) {
        String baseUrl = createUrl("data-store/set");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("data", data);
        parameters.put("key", name);

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("key", name + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    public HttpRequest buildStoreUserDataRequest(String username, String userToken, String name, String data) {
        String baseUrl = createUrl("data-store/set");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);
        parameters.put("data", data);
        parameters.put("key", name);
        parameters.put("user_token", userToken);

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("user_token", userToken + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    private String createUrl(String method) {
        StringBuilder builder = new StringBuilder(BASE_URL);
        builder.append("v").append(version).append("/");
        builder.append(method);
        return builder.toString();
    }

    private Map<String, String> createParameterMap() {
        return new LinkedHashMap<String, String>();
    }

    private Map<String, String> createParameterMap(Map<String, String> existing) {
        return new LinkedHashMap<String, String>(existing);
    }
}
