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

        Map<String, String> parameters = createInitialUserParameterMap(username);

        Map<String, String> signatureParameters = createUserSignatureParameterMap(userToken, parameters);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildAchievedTrophyRequest(String username, String userToken, String trophyId) {
        String baseUrl = createUrl("trophies/add-achieved");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("trophy_id", trophyId);

        Map<String, String> signatureParameters = createUserSignatureParameterMap(userToken, parameters);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildTrophyRequest(String username, String userToken, String trophyId) {
        String baseUrl = createUrl("trophies/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("trophy_id", trophyId);

        Map<String, String> signatureParameters = createUserSignatureParameterMap(userToken, parameters);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        request.addParameter("user_token", userToken);
        return request;
    }

    public HttpRequest buildTrophiesRequest(String username, String userToken, String achieved) {
        String baseUrl = createUrl("trophies/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("achieved", achieved);

        Map<String, String> signatureParameters = createUserSignatureParameterMap(userToken, parameters);

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

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("data", data);
        parameters.put("key", name);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    public HttpRequest buildRemoveUserDataRequest(String username, String userToken, String name) {
        String baseUrl = createUrl("data-store/remove");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("key", name);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    public HttpRequest buildRemoveGameDataRequest(String name) {
        String baseUrl = createUrl("data-store/remove");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("key", name);

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("key", name + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    public HttpRequest buildGameDataKeysRequest() {
        String baseUrl = createUrl("data-store/get-keys");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("game_id", String.valueOf(gameId) + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    public HttpRequest buildGetGameDataRequest(String name) {
        String baseUrl = createUrl("data-store/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("format", "dump");
        parameters.put("key", name);

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("key", name + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    public HttpRequest buildUserDataKeysRequest(String username, String userToken) {
        String baseUrl = createUrl("data-store/get-keys");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    public HttpRequest buildGetUserDataRequest(String username, String userToken, String name) {
        String baseUrl = createUrl("data-store/");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("format", "dump");
        parameters.put("key", name);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    public HttpRequest buildAllHighscoresRequest(int limit) {
        String baseUrl = createUrl("scores");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("limit", String.valueOf(limit));

        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(parameters);
        signatureParameters.put("limit", limit + privateKey);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, signatureParameters));
        return request;
    }

    public HttpRequest buildUserHighscoresRequest(String username, String userToken, int limit) {
        String baseUrl = createUrl("scores");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("limit", String.valueOf(limit));
        parameters.put("username", username);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    public HttpRequest buildUserAchievedHighscoreRequest(String username, String userToken, String displayedText, int score, String extra) {
        String baseUrl = createUrl("scores/add");
        HttpRequest request = new HttpRequest(baseUrl);

        Map<String, String> parameters = createInitialUserParameterMap(username);
        parameters.put("sort", String.valueOf(score));
        parameters.put("score", displayedText);
        parameters.put("extra_data", extra);
        parameters.put("user_token", userToken);

        request.addParameters(parameters);
        request.addParameter("signature", signatureFactory.build(baseUrl, createUserSignatureParameterMap(userToken, parameters)));
        return request;
    }

    private Map<String, String> createUserSignatureParameterMap(String userToken, Map<String, String> existingParameters) {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>(existingParameters);
        signatureParameters.put("user_token", userToken + privateKey);
        return signatureParameters;
    }

    private Map<String, String> createInitialUserParameterMap(String username) {
        Map<String, String> parameters = createParameterMap();
        parameters.put("game_id", String.valueOf(gameId));
        parameters.put("username", username);
        return parameters;
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

}
