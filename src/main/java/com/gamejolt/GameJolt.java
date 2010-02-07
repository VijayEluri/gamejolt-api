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

package com.gamejolt;

import com.gamejolt.net.HttpRequest;
import com.gamejolt.net.HttpResponse;
import com.gamejolt.net.Properties;
import com.gamejolt.net.RequestFactory;


public class GameJolt {
    private boolean verbose;
    private int gameId;
    private String privateKey;
    private RequestFactory requestFactory;
    private boolean verified;
    private String username;
    private String userToken;

    public GameJolt(int gameId, String privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
        this.requestFactory = new RequestFactory(gameId, this.privateKey);
    }

    public boolean verifyUser(String username, String userToken) {
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        Properties properties = new Properties(processRequest(request));
        verified = properties.getBoolean("success");
        if (verified) {
            this.username = username;
            this.userToken = userToken;
        }
        return verified;
    }

    public boolean achievedTrophy(int trophyId) throws UnverifiedUserException {
        if (!verified) {
            throw new UnverifiedUserException();
        }
        HttpRequest request = requestFactory.buildAchievedTrophyRequest(username, userToken, String.valueOf(trophyId));
        Properties properties = new Properties(processRequest(request));
        return properties.getBoolean("success");
    }

    private String processRequest(HttpRequest request) {
        if (verbose) System.out.println("REQUEST: " + request.getUrl());
        HttpResponse response = request.doGet();
        String value = response.getContentAsString();
        if (verbose) System.out.println("RESPONSE: " + value);
        return value;
    }

    protected void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
