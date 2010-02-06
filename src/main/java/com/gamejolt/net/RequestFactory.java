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


public class RequestFactory {
    private static final String BASE_URL = "http://gamejolt.com/api/game/";
    private static final String DEFAULT_VERSION = "1";

    private SignatureFactory signatureFactory;
    private String version = DEFAULT_VERSION;

    public RequestFactory(String privateKey) {
        this(new SignatureFactory(privateKey));
    }

    protected RequestFactory(SignatureFactory signatureFactory) {
        this.signatureFactory = signatureFactory;
    }

    public HttpRequest buildVerifyUserRequest(int gameId, String username, String userToken) {
        String baseUrl = createUrl("users/auth/");
        HttpRequest request = new HttpRequest(baseUrl);
        request.addParameter("game_id", gameId);
        request.addParameter("username", username);
        request.addParameter("signature", signatureFactory.build(baseUrl, gameId, username, userToken));
        request.addParameter("user_token", userToken);
        return request;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String createUrl(String method) {
        StringBuilder builder = new StringBuilder(BASE_URL);
        builder.append("v").append(version).append("/");
        builder.append(method);
        return builder.toString();
    }
}
