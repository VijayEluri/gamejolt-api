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

import com.gamejolt.net.*;

import java.util.List;


public class GameJolt {
    private boolean verbose;
    private int gameId;
    private String privateKey;
    private RequestFactory requestFactory;
    private boolean verified;
    private String username;
    private String userToken;
    private TrophyResponseParser trophyParser;
    private PropertiesParser propertiesParser = new PropertiesParser();

    public GameJolt(int gameId, String privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
        this.requestFactory = new RequestFactory(gameId, this.privateKey);
        this.trophyParser = new TrophyResponseParser();
    }

    public boolean verifyUser(String username, String userToken) {
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        Properties properties = propertiesParser.parseProperties(processRequest(request));
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
        Properties properties = propertiesParser.parseProperties(processRequest(request));
        return properties.getBoolean("success");
    }

    public Trophy getTrophy(int trophyId) throws UnverifiedUserException {
        if (!verified) {
            throw new UnverifiedUserException();
        }
        HttpRequest request = requestFactory.buildTrophyRequest(username, userToken, String.valueOf(trophyId));

        List<Trophy> trophies = trophyParser.parse(processRequest(request));
        if (trophies.size() == 0) return null;
        return trophies.get(0);
    }

    public List<Trophy> getAllTrophies() throws UnverifiedUserException {
        return getTrophies("empty");
    }

    public List<Trophy> getAchievedTrophies() throws UnverifiedUserException {
        return getTrophies("true");
    }

    public List<Trophy> getUnachievedTrophies() throws UnverifiedUserException {
        return getTrophies("false");
    }

    private String processRequest(HttpRequest request) {
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("REQUEST");
            System.out.println("-----------------------");
            System.out.println(request.getUrl());
            System.out.println("-----------------------");
        }
        HttpResponse response = request.doGet();
        String value = response.getContentAsString();
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("RESPONSE");
            System.out.println("-----------------------");
            System.out.println(value);
            System.out.println("-----------------------");
        }
        return value;
    }

    protected void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    protected void setTrophyParser(TrophyResponseParser trophyParser) {
        this.trophyParser = trophyParser;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private List<Trophy> getTrophies(String achieved) {
        if (!verified) throw new UnverifiedUserException();
        return trophyParser.parse(processRequest(requestFactory.buildTrophiesRequest(username, userToken, achieved)));
    }
}
