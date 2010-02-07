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

    /**
     * Let the Game Jolt experience begin! :)
     * <p/>
     * The information needed here can all be acquired as follows:
     * <p/>
     * Your Dashboard -> Pick your game -> Manage Achievements -> Game Info
     *
     * @param gameId     - the id of your Game
     * @param privateKey - your personal privatekey
     */
    public GameJolt(int gameId, String privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
        this.requestFactory = new RequestFactory(gameId, this.privateKey);
        this.trophyParser = new TrophyResponseParser();
    }

    /**
     * Verifies your current game player is a verified user of Game Jolt
     *
     * @param username  - player's username
     * @param userToken - player's usertoken
     * @return <p>true - player is a valid user</p>
     *         <p>false - player is not a valid user</p>
     */
    public boolean verifyUser(String username, String userToken) {
        if (doesNotNeedToVerify(username)) {
            return true;
        }
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        Properties properties = propertiesParser.parseProperties(processRequest(request));
        verified = properties.getBoolean("success");
        if (verified) {
            this.username = username;
            this.userToken = userToken;
        }
        return verified;
    }

    /**
     * The current player has achieved a trophy with the given id
     *
     * @param trophyId - the id of the trophy that has been achieved
     * @return <p>true - player's profile has been updated with the achievement<p>
     *         <p>false - player has already achieved this trophy</p>
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public boolean achievedTrophy(int trophyId) throws UnverifiedUserException {
        if (!verified) {
            throw new UnverifiedUserException();
        }
        HttpRequest request = requestFactory.buildAchievedTrophyRequest(username, userToken, String.valueOf(trophyId));
        Properties properties = propertiesParser.parseProperties(processRequest(request));
        return properties.getBoolean("success");
    }

    /**
     * Retrieve state of the given trophy achievement for the current player
     *
     * @param trophyId - the id of the trophy
     * @return null if the given trophyId did not match a trophy, otherwise return the trophy
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public Trophy getTrophy(int trophyId) throws UnverifiedUserException {
        if (!verified) {
            throw new UnverifiedUserException();
        }
        HttpRequest request = requestFactory.buildTrophyRequest(username, userToken, String.valueOf(trophyId));

        List<Trophy> trophies = trophyParser.parse(processRequest(request));
        if (trophies.size() == 0) return null;
        return trophies.get(0);
    }

    /**
     * Retreives all trophies available for your game
     *
     * @return a list of trophies available
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<Trophy> getAllTrophies() throws UnverifiedUserException {
        return getTrophies("empty");
    }

    /**
     * Retreives all trophies achieved by the current player
     *
     * @return a list of achieved trophies
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<Trophy> getAchievedTrophies() throws UnverifiedUserException {
        return getTrophies("true");
    }

    /**
     * Retreives all trophies that have not be achieved yet by the current player
     *
     * @return a list of unachieved trophies
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<Trophy> getUnachievedTrophies() throws UnverifiedUserException {
        return getTrophies("false");
    }

    /**
     * Toggle on/off to see or not see the HttpRequests and HttpResponses
     *
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private String processRequest(HttpRequest request) {
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("REQUEST");
            System.out.println("-----------------------");
            System.out.println(request.getUrl());
            System.out.println("-----------------------");
            System.out.flush();
        }
        HttpResponse response = request.doGet();
        String value = response.getContentAsString();
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("RESPONSE");
            System.out.println("-----------------------");
            System.out.println(value);
            System.out.println("-----------------------");
            System.out.flush();
        }
        return value;
    }

    private boolean doesNotNeedToVerify(String username) {
        return verified && username.equals(this.username);
    }

    protected void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    protected void setTrophyParser(TrophyResponseParser trophyParser) {
        this.trophyParser = trophyParser;
    }

    protected void setPropertiesParser(PropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    private List<Trophy> getTrophies(String achieved) {
        if (!verified) throw new UnverifiedUserException();
        return trophyParser.parse(processRequest(requestFactory.buildTrophiesRequest(username, userToken, achieved)));
    }

}
