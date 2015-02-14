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
import com.gamejolt.net.HttpResponse;
import com.gamejolt.net.HttpResponseHandlerAdapter;
import com.gamejolt.net.PropertiesListHttpResponseHandler;
import com.gamejolt.net.RequestFactory;
import com.gamejolt.net.SuccessResponseHandler;
import com.gamejolt.net.TrophyHttpResponseHandler;
import com.gamejolt.util.PropertiesParser;
import com.gamejolt.util.TrophyParser;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.gamejolt.util.MessageFormat.format;


public class GameJolt {
    private static final String NULL_BYTES = "ObjectSerializer serialized {0} to a null byte array, please give at least an empty byte array";
    private static final String STORE_NULL_OBJECT = "You supplied a null object for storing. This is invalid, if you would like to remove data, please use the {0} method";

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
        this.requestFactory = new RequestFactory(gameId, privateKey);
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
     * @param listener  - the callback that is notified when a user is successfully or fails verification
     */
    public void verifyUser(final String username, final String userToken, final UserVerificationListener listener) {
        if (doesNotNeedToVerify(username, userToken)) {
            return;
        }
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                verified = true;
                GameJolt.this.username = username;
                GameJolt.this.userToken = userToken;
                listener.verified(username);
            }

            protected void handleFailure() {
                verified = false;
                listener.failedVerification(username);
            }
        });
    }

    /**
     * The current player has achieved a trophy with the given id
     *
     * @param trophyId - the id of the trophy that has been achieved
     * @param listener - the callback notified when the trophy is successfully achieved
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void achievedTrophy(final int trophyId, final TrophyAchievedListener listener) throws UnverifiedUserException {
        assertVerified();
        HttpRequest request = requestFactory.buildAchievedTrophyRequest(username, userToken, String.valueOf(trophyId));
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                getTrophy(trophyId, new TrophyLookupListenerAdaptor() {
                    public void found(Trophy trophy) {
                        listener.achieved(trophy);
                    }
                });
            }
        });
    }

    /**
     * Retrieve state of the given trophy achievement for the current player
     *
     * @param trophyId - the id of the trophy
     * @param listener - the callback that notifies you if the trophy is found or not
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void getTrophy(final int trophyId, final TrophyLookupListener listener) throws UnverifiedUserException {
        assertVerified();
        HttpRequest request = requestFactory.buildTrophyRequest(username, userToken, String.valueOf(trophyId));
        request.execute(new TrophyHttpResponseHandler(trophyParser) {
            protected void handle(List<Trophy> trophies) {
                if (trophies.isEmpty()) {
                    listener.notFound(trophyId);
                } else {
                    listener.found(trophies.get(0));
                }
            }
        });
    }

    /**
     * Retrieves all trophies available for your game
     *
     * @param listener - the callback that will be notified when the trophies are found
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void getAllTrophies(TrophiesLookupListener listener) throws UnverifiedUserException {
        getTrophies("empty", listener);
    }

    /**
     * Retrieves all trophies achieved by the current player
     *
     * @param listener - the callback that will be notified when the trophies are found
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void getAchievedTrophies(TrophiesLookupListener listener) throws UnverifiedUserException {
        getTrophies("true", listener);
    }

    /**
     * Retrieves all trophies that have not be achieved yet by the current player
     *
     * @param listener - the callback that will be notified when the trophies are found
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void getUnachievedTrophies(TrophiesLookupListener listener) throws UnverifiedUserException {
        getTrophies("false", listener);
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
     * @param listener - the callback that is notified on success
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void storeUserData(String name, Object data, final Listener listener) throws UnverifiedUserException {
        assertVerified();
        if (data == null) {
            throw new NullPointerException(format(STORE_NULL_OBJECT, "removeUserData"));
        }
        byte[] bytes = objectSerializer.serialize(data);
        if (bytes == null) {
            throw new NullPointerException(format(NULL_BYTES, data.getClass()));
        }
        HttpRequest request = requestFactory.buildStoreUserDataRequest(username, userToken, name, binarySanitizer.sanitize(bytes));
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                listener.success();
            }
        });
    }

    /**
     * Store data in the form of a custom object specific to the game
     *
     * @param name - the name given to the data
     * @param data - the data to be stored
     * @param listener - the callback that is notified on success
     */
    public void storeGameData(String name, Object data, final Listener listener) {
        if (data == null) {
            throw new NullPointerException(format(STORE_NULL_OBJECT, "removeGameData"));
        }
        byte[] bytes = objectSerializer.serialize(data);
        if (bytes == null) {
            throw new NullPointerException(format(NULL_BYTES, data.getClass()));
        }
        HttpRequest request = requestFactory.buildStoreGameDataRequest(name, binarySanitizer.sanitize(bytes));
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                listener.success();
            }
        });
    }

    /**
     * Remove user data with the given name
     *
     * @param name - the name of the data to be removed
     * @param listener - the callback that is notified on success
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void removeUserData(String name, final Listener listener) throws UnverifiedUserException {
        assertVerified();
        HttpRequest request = requestFactory.buildRemoveUserDataRequest(username, userToken, name);
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                listener.success();
            }
        });
    }

    /**
     * Remove game data with the given name
     *
     * @param name - the name of the data to be removed
     * @param listener - the callback that is notified on success
     */
    public void removeGameData(String name, final Listener listener) {
        HttpRequest request = requestFactory.buildRemoveGameDataRequest(name);
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                listener.success();
            }
        });
    }

    /**
     * Look up all the keys referencing game data
     *
     * @param listener - all callback that provides the keys
     */
    public void getGameDataKeys(final DataKeysListener listener) {
        HttpRequest request = requestFactory.buildGameDataKeysRequest();
        request.execute(new PropertiesListHttpResponseHandler(propertiesParser) {
            protected void handle(List<String> values) {
                listener.keys(values);
            }
        });
    }

    /**
     * Look up all the keys referencing user data
     *
     * @param listener - a callback that provides the keys
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void getUserDataKeys(final DataKeysListener listener) throws UnverifiedUserException {
        assertVerified();
        HttpRequest request = requestFactory.buildUserDataKeysRequest(username, userToken);
        request.execute(new PropertiesListHttpResponseHandler(propertiesParser) {
            protected void handle(List<String> values) {
                values.remove("success");
                listener.keys(values);
            }
        });
    }

    /**
     * Clear all game data stored
     *
     * @param listener - a callback that notifies when all the data has been deleted successfully
     */
    public void clearAllGameData(final Listener listener) {
        getGameDataKeys(new DataKeysListener() {
            public void keys(final List<String> keys) {
                final ArrayList<String> deletedData = new ArrayList();
                for (final String key : keys) {
                    removeGameData(key, new Listener() {
                        public void success() {
                            deletedData.add(key);

                            if (deletedData.size() == keys.size()) {
                                listener.success();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * Clear all user data stored
     *
     * @param listener - a callback that notifies when all the data has been deleted successfully
     *
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public void clearAllUserData(final Listener listener) throws UnverifiedUserException {
        getUserDataKeys(new DataKeysListener() {
            public void keys(final List<String> keys) {
                final ArrayList<String> deletedData = new ArrayList();
                for (final String key : keys) {
                    removeUserData(key, new Listener() {
                        public void success() {
                            deletedData.add(key);

                            if (deletedData.size() == keys.size()) {
                                listener.success();
                            }
                        }
                    });
                }
            }
        });
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
        assertVerified();
        HttpRequest request = requestFactory.buildGetUserDataRequest(username, userToken, name);
        return deserializeData(request);
    }

    /**
     * Loads all the game data stored
     *
     * @return a Map<String,Object> containing all persisted data
     */
    public Map<String, Object> loadAllGameData() {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        getGameDataKeys(new DataKeysListener() {
            public void keys(List<String> keys) {
                for (String key : keys) {
                    data.put(key, getGameData(key));
                }
            }
        });
        return data;
    }

    /**
     * Loads all the user data stored
     *
     * @return a Map<String,Object> containing all persisted data
     * @throws UnverifiedUserException is thrown if the given player has not be verified yet
     */
    public Map<String, Object> loadAllUserData() throws UnverifiedUserException {
        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        getUserDataKeys(new DataKeysListener() {
            public void keys(List<String> keys) {
                for (String key : keys) {
                    data.put(key, getUserData(key));
                }
            }
        });
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
        assertVerified();
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
        assertVerified();
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

    @Deprecated
    private boolean wasSuccessful(HttpRequest request) {
        final AtomicBoolean result = new AtomicBoolean(false);
        request.execute(new SuccessResponseHandler(propertiesParser) {
            protected void handleSuccess() {
                result.set(true);
            }

            protected void handleFailure() {
                result.set(false);
            }
        });

        return result.get();
    }

    @Deprecated
    private String processRequest(HttpRequest request) {
        final StringBuilder stringBuilder = new StringBuilder();
        request.execute(new HttpResponseHandlerAdapter() {
            public void handle(HttpResponse response) {
                stringBuilder.append(response.getContentAsString());
            }
        });
        return stringBuilder.toString();
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

    private void getTrophies(String achieved, final TrophiesLookupListener listener) {
        assertVerified();
        HttpRequest request = requestFactory.buildTrophiesRequest(username, userToken, achieved);
        request.execute(new TrophyHttpResponseHandler(trophyParser) {
            protected void handle(List<Trophy> trophies) {
                listener.foundTrophies(trophies);
            }
        });
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

    protected void assertVerified() {
        if (!verified) {
            throw new UnverifiedUserException();
        }
    }

    protected void setHighscoreParser(HighscoreParser highscoreParser) {
        this.highscoreParser = highscoreParser;
    }
}
