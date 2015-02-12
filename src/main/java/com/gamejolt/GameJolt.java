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

import com.gamejolt.highscore.Highscore;
import com.gamejolt.highscore.HighscoreParser;
import com.gamejolt.io.BinarySanitizer;
import com.gamejolt.io.ObjectSerializer;
import com.gamejolt.io.StandardJavaObjectSerializer;
import com.gamejolt.net.HttpRequest;
import com.gamejolt.net.RequestFactory;
import com.gamejolt.util.Properties;
import com.gamejolt.util.PropertiesParser;
import com.gamejolt.util.TrophyParser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;


public class GameJolt {
    private static final String NULL_BYTES = "ObjectSerializer serialized {0} to a null byte array, please give at least an empty byte array";
    private static final String STORE_NULL_OBJECT = "You supplied a null object for storing. This is invalid, if you would like to remove data, please use the {0} method";

    private boolean verbose;
    private int gameId;
    private String privateKey;
    private RequestFactory requestFactory;
    private boolean verified;
    private String username;
    private String userToken;
    private TrophyParser trophyParser;
    private PropertiesParser propertiesParser;
    private ObjectSerializer objectSerializer;
    private BinarySanitizer binarySanitizer;
    private NumberFormat highscoreFormatter;
    private HighscoreParser highscoreParser;

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
        this.trophyParser = new TrophyParser();
        this.propertiesParser = new PropertiesParser();
        this.binarySanitizer = new BinarySanitizer();
        this.objectSerializer = new StandardJavaObjectSerializer();
        this.highscoreFormatter = new DecimalFormat("###,###,###,###,###");
        this.highscoreParser = new HighscoreParser();
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
        if (doesNotNeedToVerify(username, userToken)) {
            return true;
        }
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        verified = wasSuccessful(request);
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
        return wasSuccessful(requestFactory.buildAchievedTrophyRequest(username, userToken, String.valueOf(trophyId)));
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
        requestFactory.setVerbose(verbose);
    }

    /**
     * Store data in the form of a custom object specific to the user
     *
     * @param name - the name given to the data
     * @param data - the data to be stored
     * @return <p>true - successfully stored data</p>
     *         <p>false - failed to store the data</p>
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public boolean storeUserData(String name, Object data) throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        if (data == null) {
            throw new NullPointerException(format(STORE_NULL_OBJECT, "removeUserData"));
        }
        byte[] bytes = objectSerializer.serialize(data);
        if (bytes == null) {
            throw new NullPointerException(format(NULL_BYTES, data.getClass()));
        }
        return wasSuccessful(requestFactory.buildStoreUserDataRequest(username, userToken, name, binarySanitizer.sanitize(bytes)));
    }

    /**
     * Store data in the form of a custom object specific to the game
     *
     * @param name - the name given to the data
     * @param data - the data to be stored
     * @return <p>true - successfully stored data</p>
     *         <p>false - failed to store the data</p>
     */
    public boolean storeGameData(String name, Object data) {
        if (data == null) throw new NullPointerException(format(STORE_NULL_OBJECT, "removeGameData"));
        byte[] bytes = objectSerializer.serialize(data);
        if (bytes == null) {
            throw new NullPointerException(format(NULL_BYTES, data.getClass()));
        }
        return wasSuccessful(requestFactory.buildStoreGameDataRequest(name, binarySanitizer.sanitize(bytes)));
    }

    /**
     * Remove user data with the given name
     *
     * @param name - the name of the data to be removed
     * @return <p>true - was successfully removed</p>
     *         <p>false - was not removed or does not exist</p>
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public boolean removeUserData(String name) throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        return wasSuccessful(requestFactory.buildRemoveUserDataRequest(username, userToken, name));
    }

    /**
     * Remove game data with the given name
     *
     * @param name - the name of the data to be removed
     * @return <p>true - was successfully removed</p>
     *         <p>false - was not removed or does not exist</p>
     */
    public boolean removeGameData(String name) {
        return wasSuccessful(requestFactory.buildRemoveGameDataRequest(name));
    }

    /**
     * Look up all the keys referencing game data
     *
     * @return a list containing all the keys to game data
     */
    public List<String> getGameDataKeys() {
        HttpRequest request = requestFactory.buildGameDataKeysRequest();
        return propertiesParser.parseToList(processRequest(request), "key");
    }

    /**
     * Look up all the keys referencing user data
     *
     * @return a list containing all the keys to user data
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<String> getUserDataKeys() throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        HttpRequest request = requestFactory.buildUserDataKeysRequest(username, userToken);
        List<String> keys = propertiesParser.parseToList(processRequest(request), "key");
        keys.remove("success");
        return keys;
    }

    /**
     * Clear all game data stored
     */
    public void clearAllGameData() {
        List<String> keys = getGameDataKeys();
        for (String key : keys) {
            removeGameData(key);
        }
    }

    /**
     * Clear all user data stored
     *
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void clearAllUserData() throws UnverifiedUserException {
        List<String> keys = getUserDataKeys();
        for (String key : keys) {
            removeUserData(key);
        }
    }

    /**
     * Get persisted data by the given name
     *
     * @param name - the name given to the data stored
     * @return returns null if no data was found with that name, otherwise return the object stored
     */
    public Object getGameData(String name) {
        HttpRequest request = requestFactory.buildGetGameDataRequest(name);
        return deserializeData(request);
    }

    /**
     * Get persisted data by the given name
     *
     * @param name - the name given to the data stored
     * @return returns null if no data was found with that name, otherwise return the object stored
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public Object getUserData(String name) throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        HttpRequest request = requestFactory.buildGetUserDataRequest(username, userToken, name);
        return deserializeData(request);
    }

    /**
     * Loads all the game data stored
     *
     * @return a Map<String,Object> containing all persisted data
     */
    public Map<String, Object> loadAllGameData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        List<String> keys = getGameDataKeys();
        for (String key : keys) {
            data.put(key, getGameData(key));
        }
        return data;
    }

    /**
     * Loads all the user data stored
     *
     * @return a Map<String,Object> containing all persisted data
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public Map<String, Object> loadAllUserData() throws UnverifiedUserException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        List<String> keys = getUserDataKeys();
        for (String key : keys) {
            data.put(key, getUserData(key));
        }
        return data;
    }

    /**
     * Grab a limited number of highscores
     *
     * @param limit - the maximum number of scores
     * @return a List of highscores
     */
    public List<Highscore> getAllHighscores(int limit) {
        return highscoreParser.parse(processRequest(requestFactory.buildAllHighscoresRequest(limit)));
    }

    /**
     * Grab the top 10 highscores
     *
     * @return a List of highscores
     */
    public List<Highscore> getTop10Highscores() {
        return getAllHighscores(10);
    }

    /**
     * Grab a limited number of user highscores
     *
     * @param limit - the maximum number of scores
     * @return a List of highscores
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<Highscore> getUserHighscores(int limit) throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        return highscoreParser.parse(processRequest(requestFactory.buildUserHighscoresRequest(username, userToken, limit)));
    }

    /**
     * Grab the user's top 10 highscores
     *
     * @return a List of highscores
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public List<Highscore> getTop10UserHighscores() throws UnverifiedUserException {
        return getUserHighscores(10);
    }

    /**
     * User has achieved a new highscore
     *
     * @param displayedText - the text to be displayed on Game Jolt
     * @param score         - the literal score which will be used to determine if this score is higher than the other scores
     * @param extra         - extra data to be displayed
     * @return <p>true - successfully added highscore</p><p>false - failed adding highscore</p>
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public boolean userAchievedHighscore(String displayedText, int score, String extra) throws UnverifiedUserException {
        if (!verified) throw new UnverifiedUserException();
        return wasSuccessful(requestFactory.buildUserAchievedHighscoreRequest(username, userToken, displayedText, score, extra));
    }

    /**
     * User has achieved a new highscore
     * <p/>
     * The score display formatting is using Java's NumberFormat class.
     * <p/>
     * By default the score's formatting is "###,###,###,###,###", if you do not like this formatting you can easily change the formatting by calling the setter for the HighscoreFormatter.
     *
     * @param score - the score achieved
     * @return <p>true - successfully added highscore</p><p>false - failed adding highscore</p>
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public boolean userAchievedHighscore(int score) throws UnverifiedUserException {
        return userAchievedHighscore(highscoreFormatter.format(score), score, "");
    }

    public void setObjectSerializer(com.gamejolt.io.ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    public void setHighscoreFormatter(NumberFormat highscoreFormatter) {
        this.highscoreFormatter = highscoreFormatter;
    }

    protected void setBinarySanitizer(BinarySanitizer binarySanitizer) {
        this.binarySanitizer = binarySanitizer;
    }

    private boolean wasSuccessful(HttpRequest request) {
        Properties properties = propertiesParser.parseProperties(processRequest(request));
        return properties.getBoolean("success");
    }

    private String processRequest(HttpRequest request) {
        return request.execute();
    }

    private boolean doesNotNeedToVerify(String username, String userToken) {
        return verified && username.equals(this.username) && userToken.equals(this.userToken);
    }

    protected void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    protected void setTrophyParser(TrophyParser trophyParser) {
        this.trophyParser = trophyParser;
    }

    protected void setPropertiesParser(PropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    private List<Trophy> getTrophies(String achieved) {
        if (!verified) throw new UnverifiedUserException();
        return trophyParser.parse(processRequest(requestFactory.buildTrophiesRequest(username, userToken, achieved)));
    }

    private Object deserializeData(HttpRequest request) {
        String responseContent = processRequest(request);
        String[] lines = responseContent.split("\r\n|\n");
        String successOrFailure = lines[0];
        if ("SUCCESS".equalsIgnoreCase(successOrFailure)) {
            String data = lines[1];
            return objectSerializer.deserialize(binarySanitizer.unsanitize(data));
        }
        return null;
    }

    protected void setHighscoreParser(HighscoreParser highscoreParser) {
        this.highscoreParser = highscoreParser;
    }
}
