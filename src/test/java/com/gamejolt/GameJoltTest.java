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
import com.gamejolt.net.HttpRequest;
import com.gamejolt.net.MockHttpRequest;
import com.gamejolt.net.RequestFactory;
import com.gamejolt.util.Properties;
import com.gamejolt.util.PropertiesParser;
import com.gamejolt.util.TrophyParser;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameJoltTest {
    private static final Object OUR_OBJECT = new Object();
    private static final String USERNAME = "username";
    private static final String USER_TOKEN = "userToken";
    @Mock private RequestFactory requestFactory;
    @InjectMocks private GameJolt gameJolt = new GameJolt(1111, "private-key");
    @Mock private TrophyParser trophyParser;
    @Mock private PropertiesParser propertiesParser;
    @Mock private ObjectSerializer objectSerializer;
    @Mock private BinarySanitizer binarySanitizer;
    @Mock private HighscoreParser highscoreParser;
    private MockUserVerificationListener userVerificationListener;
    private MockTrophyAchievedListener trophyAchievedListener;
    private MockTrophyLookupListener trophyLookupListener;
    private MockTrophiesLookupListener trophiesLookupListener;
    private MockListener listener;
    private MockDataKeysListener dataKeyListener;

    @Before
    public void setUp() throws Exception {
        userVerificationListener = new MockUserVerificationListener();
        trophyAchievedListener = new MockTrophyAchievedListener();
        trophyLookupListener = new MockTrophyLookupListener();
        trophiesLookupListener = new MockTrophiesLookupListener();
        listener = new MockListener();
        dataKeyListener = new MockDataKeysListener();
    }

    @Test
    public void test_userAchievedHighscore_Success_ChangeBuiltInFormatting() {
        gameJolt.setHighscoreFormatter(new DecimalFormat("$ #,###.00"));

        hasAVerifiedUser();
        whenUserHasAchievedAHighScoreSuccessfully();

        gameJolt.userAchievedHighscore(1234);

        assertHighScoreWasPassedServer("$ 1,234.00", 1234, "");
    }

    @Test
    public void test_userAchievedHighscore_Success_UsingBuiltInFormatting() {
        hasAVerifiedUser();
        whenUserHasAchievedAHighScoreSuccessfully();

        gameJolt.userAchievedHighscore(10000000);

        assertHighScoreWasPassedServer("10,000,000", 10000000, "");
    }

    @Test
    public void shouldReturnTrueWhenTheHighScoreWasStoredSuccessfully() {
        hasAVerifiedUser();
        whenUserHasAchievedAHighScoreSuccessfully();

        assertTrue(gameJolt.userAchievedHighscore(100));
    }

    @Test
    public void test_userAchievedHighscore_Failed() {
        hasAVerifiedUser();

        whenAttemptToStoreAHighScore(false);

        assertFalse(gameJolt.userAchievedHighscore("displayed", 10, "extra"));
    }

    @Test
    public void test_userAchievedHighscore_Success() {
        hasAVerifiedUser();
        whenUserHasAchievedAHighScoreSuccessfully();

        gameJolt.userAchievedHighscore("displayed", 10, "extra");

        assertHighScoreWasPassedServer("displayed", 10, "extra");
    }

    @Test
    public void shouldReturnTrueWhenHighscoreDataWasStoredSuccessfully() {
        hasAVerifiedUser();
        whenUserHasAchievedAHighScoreSuccessfully();

        assertTrue(gameJolt.userAchievedHighscore("displayed", 10, "extra"));
    }

    @Test
    public void test_userAchievedHighscore_UnverifiedUser() {
        try {
            gameJolt.userAchievedHighscore("displayed", 100, "extra");
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getTop10Highscores() {
        List<Highscore> parseScores = Arrays.asList(new Highscore());
        whenWeQueryForALimitedNumberOfHighscores(10, parseScores);

        assertSame(parseScores, gameJolt.getTop10Highscores());
    }

    @Test
    public void test_getAllHighscores_WithLimit() {
        List<Highscore> parseScores = Arrays.asList(new Highscore());
        whenWeQueryForALimitedNumberOfHighscores(100, parseScores);

        assertSame(parseScores, gameJolt.getAllHighscores(100));
    }

    @Test
    public void test_getUserHighscores_WithLimit_NotVerified() {
        try {
            gameJolt.getUserHighscores(100);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getUserHighscores_WithLimit() {
        hasAVerifiedUser();
        List<Highscore> parseScores = Arrays.asList(new Highscore());
        whenWeQueryForUserHighscores(100, parseScores);

        assertSame(parseScores, gameJolt.getUserHighscores(100));
    }

    @Test
    public void test_getTop10UserHighscores() {
        hasAVerifiedUser();
        List<Highscore> parseScores = Arrays.asList(new Highscore());
        whenWeQueryForUserHighscores(10, parseScores);

        assertSame(parseScores, gameJolt.getTop10UserHighscores());
    }

    @Test
    public void test_getTop10UserHighscores_NotVerified() {
        try {
            gameJolt.getTop10UserHighscores();
            fail();
        } catch (UnverifiedUserException err) {

        }
    }

    @Test
    public void test_loadAllGameData_MultipleKeys() {
        whenWeQueryForGameDataKeys("key1", "key2");
        whenWeQueryForGameData("key1", OUR_OBJECT);
        whenWeQueryForGameData("key2", OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllGameData();

        assertEquals(2, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
        assertSame(OUR_OBJECT, data.get("key2"));
    }

    @Test
    public void test_loadAllUserData_MultipleKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys("key1", "key2");
        whenWeQueryForUserData("key1", OUR_OBJECT);
        whenWeQueryForUserData("key2", OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertEquals(2, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
        assertSame(OUR_OBJECT, data.get("key2"));
    }

    @Test
    public void test_loadAllGameData_SingleKey() {
        whenWeQueryForGameDataKeys("key1");
        whenWeQueryForGameData("key1", OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllGameData();

        assertNotNull(data);
        assertEquals(1, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
    }

    @Test
    public void test_loadAllUserData_SingleKey() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys("key1");
        whenWeQueryForUserData("key1", OUR_OBJECT);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertNotNull(data);
        assertEquals(1, data.size());
        assertSame(OUR_OBJECT, data.get("key1"));
    }

    @Test
    public void test_loadAllGameData_NoKeys() {
        whenWeQueryForGameDataKeys();

        Map<String, Object> data = gameJolt.loadAllGameData();
        assertNotNull(data);
        assertEquals(0, data.size());
        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_loadAllUserData_NoKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeysAndNoneAreFound();

        Map<String, Object> data = gameJolt.loadAllUserData();
        assertNotNull(data);
        assertEquals(0, data.size());
        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_loadAllUserData_UnverifiedUser() {
        try {
            gameJolt.loadAllUserData();
            fail();
        } catch (UnverifiedUserException err) {

        }

        verifyZeroInteractions(requestFactory, binarySanitizer, objectSerializer);
    }

    @Test
    public void test_getUserData_NoMatchingObject() {
        hasAVerifiedUser();
        whenWeQueryForUserDataAndItFails();

        assertNull(gameJolt.getUserData("key-value"));
    }

    @Test
    public void test_getGameData_NoMatchingObject() {
        whenWeQueryForGameDataAndItFails("key-value");

        assertNull(gameJolt.getGameData("key-value"));

        verifyZeroInteractions(binarySanitizer, objectSerializer);
    }

    @Test
    public void test_getGameData_MatchingObject_WindowsLineEndings() {
        whenWeQueryForGameData("key-value", OUR_OBJECT);

        assertSame(OUR_OBJECT, gameJolt.getGameData("key-value"));
    }

    @Test
    public void test_getUserData_MatchingObject_WindowsLineEndings() {
        hasAVerifiedUser();
        whenWeQueryForUserData("key-value", OUR_OBJECT);

        assertSame(OUR_OBJECT, gameJolt.getUserData("key-value"));
    }

    @Test
    public void test_getGameData_MatchingObject_UnixLineEndings() {
        whenWeQueryForGameDataWithUnixLineEndings("key-value", OUR_OBJECT);

        Object obj = gameJolt.getGameData("key-value");

        assertNotNull(obj);
        assertSame(OUR_OBJECT, obj);
    }

    @Test
    public void test_getUserData_MatchingObject_UnixLineEndings() {
        hasAVerifiedUser();
        whenWeQueryForUserData("key-value", OUR_OBJECT);

        Object obj = gameJolt.getUserData("key-value");

        assertNotNull(obj);
        assertSame(OUR_OBJECT, obj);
    }

    @Test
    public void test_getUserData_UnverifiedUser() {
        try {
            gameJolt.getUserData("key-value");
            fail();
        } catch (UnverifiedUserException err) {

        }
    }

    @Test
    public void test_clearAllGameData_MultipleKeys() {
        whenWeQueryForGameDataKeys("key-value", "key-value2");
        whenWeDeleteGameData("key-value");
        whenWeDeleteGameData("key-value2");

        gameJolt.clearAllGameData(listener);

        verify(requestFactory).buildRemoveGameDataRequest("key-value");
        verify(requestFactory).buildRemoveGameDataRequest("key-value2");
        listener.assertSuccess();
    }

    @Test
    public void test_clearAllGameData_SingleKey() {
        whenWeQueryForGameDataKeys("key-value");
        whenWeDeleteGameData("key-value");

        gameJolt.clearAllGameData(listener);

        verify(requestFactory).buildRemoveGameDataRequest("key-value");
    }

    @Test
    public void test_clearAllGameData_NoKeys() {
        whenWeQueryForGameDataKeys();

        gameJolt.clearAllGameData(listener);

        verify(requestFactory).buildGameDataKeysRequest();
        verifyNoMoreInteractions(requestFactory);
    }

    @Test
    public void test_getGameDataKeys() {
        whenWeQueryForGameDataKeys("key-value");

        gameJolt.getGameDataKeys(dataKeyListener);

        dataKeyListener.assertKeys("key-value");
    }

    @Test
    public void test_clearAllUserData_MultipleKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys("key-value", "key-value2");
        whenWeRemoveUserData("key-value");
        whenWeRemoveUserData("key-value2");

        gameJolt.clearAllUserData(listener);

        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value");
        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value2");
        listener.assertSuccess();
    }

    @Test
    public void test_clearAllUserData_SingleKey() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys("key-value");
        whenWeRemoveUserData("key-value");

        gameJolt.clearAllUserData(listener);

        verify(requestFactory).buildRemoveUserDataRequest(USERNAME, USER_TOKEN, "key-value");
    }

    @Test
    public void test_clearAllUserData_NoKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeysAndNoneAreFound();

        gameJolt.clearAllUserData(listener);

        verify(requestFactory).buildVerifyUserRequest(USERNAME, USER_TOKEN);
        verify(requestFactory).buildUserDataKeysRequest(USERNAME, USER_TOKEN);
        verifyNoMoreInteractions(requestFactory);
    }

    @Test
    public void test_getUserDataKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys("key-value");

        gameJolt.getUserDataKeys(dataKeyListener);

        dataKeyListener.assertKeys("key-value");
    }

    @Test
    public void test_getGameDataKeys_NoKeys() {
        whenWeQueryForGameDataKeys();

        gameJolt.getGameDataKeys(dataKeyListener);

        dataKeyListener.assertNoKeys();
    }

    @Test
    public void test_getUserDataKeys_NoKeys() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeysAndNoneAreFound();

        gameJolt.getUserDataKeys(dataKeyListener);

        dataKeyListener.assertNoKeys();
    }

    @Test
    public void test_getUserDataKeys_Failed() {
        hasAVerifiedUser();
        whenWeExpectToQueryForAllUserDataKeys(properties(false));

        gameJolt.getUserDataKeys(dataKeyListener);

        dataKeyListener.assertNoKeys();
    }

    @Test
    public void test_getUserDataKeys_Unverified() {
        try {
            gameJolt.getUserDataKeys(dataKeyListener);
            fail();
        } catch (UnverifiedUserException err) {

        }
    }

    @Test
    public void test_removeGameData() {
        whenWeDeleteGameData("name", true);

        gameJolt.removeGameData("name", listener);

        listener.assertSuccess();
    }

    @Test
    public void test_removeGameData_Failed() {
        whenWeDeleteGameData("name", false);

        gameJolt.removeGameData("name", listener);

        listener.assertNotSuccessful();
    }

    @Test
    public void test_removeUserData_Failed() {
        hasAVerifiedUser();

        whenWeFailedToRemoveUserData("name");

        gameJolt.removeUserData("name", listener);

        listener.assertNotSuccessful();
    }

    @Test
    public void test_removeUserData() {
        hasAVerifiedUser();
        whenWeRemoveUserData("name");

        gameJolt.removeUserData("name", listener);

        listener.assertSuccess();
    }

    @Test
    public void test_removeUserData_UserNotVerified() {
        try {
            gameJolt.removeUserData("name", listener);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_storeGameData_NullObjectGiven() {
        try {
            gameJolt.storeGameData("name", (Object) null, listener);
            fail();
        } catch (NullPointerException err) {
            assertEquals("You supplied a null object for storing. This is invalid, if you would like to remove data, please use the removeGameData method", err.getMessage());
        }
    }

    @Test
    public void test_storeUserData_NullObjectGiven() {
        hasAVerifiedUser();

        try {
            gameJolt.storeUserData("name", (Object) null, listener);
            fail();
        } catch (NullPointerException err) {
            assertEquals("You supplied a null object for storing. This is invalid, if you would like to remove data, please use the removeUserData method", err.getMessage());
        }
    }


    @Test
    public void test_storeUserData_ObjectSerializerReturnsANullByteArray() {
        DummyObject obj = new DummyObject();

        hasAVerifiedUser();
        when(objectSerializer.serialize(obj)).thenReturn(null);

        try {
            gameJolt.storeUserData("name", obj, listener);
            fail();
        } catch (NullPointerException err) {
            assertEquals("ObjectSerializer serialized " + DummyObject.class + " to a null byte array, please give at least an empty byte array", err.getMessage());
        }
    }

    @Test
    public void test_storeGameData_ObjectSerializerReturnsANullByteArray() {
        DummyObject obj = new DummyObject();
        when(objectSerializer.serialize(obj)).thenReturn(null);

        try {
            gameJolt.storeGameData("name", obj, listener);
            fail();
        } catch (NullPointerException err) {
            assertEquals("ObjectSerializer serialized " + DummyObject.class + " to a null byte array, please give at least an empty byte array", err.getMessage());
        }
    }

    @Test
    public void test_storeUserData_Object() {
        DummyObject obj = new DummyObject();
        hasAVerifiedUser();

        whenWeStoreUserData("name", obj);

        gameJolt.storeUserData("name", obj, listener);

        listener.assertSuccess();
    }

    @Test
    public void test_storeGameData_Object() {
        DummyObject obj = new DummyObject();
        whenStoreGameData("name", obj);

        gameJolt.storeGameData("name", obj, listener);

        listener.assertSuccess();
    }

    @Test
    public void test_getUnachievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getUnachievedTrophies(trophiesLookupListener);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getUnachievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();

        whenWeRequestForUnachievedTrophies(trophies);

        gameJolt.getUnachievedTrophies(trophiesLookupListener);

        trophiesLookupListener.assertLookedUp(trophies);
    }

    @Test
    public void test_getAchievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getAchievedTrophies(trophiesLookupListener);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getAchievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();
        whenWeRequestForAchievedTrophies(trophies);

        gameJolt.getAchievedTrophies(trophiesLookupListener);

        trophiesLookupListener.assertLookedUp(trophies);
    }

    @Test
    public void test_getAllTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser();

        whenWeRequestForAllTrophies(trophies);

        gameJolt.getAllTrophies(trophiesLookupListener);

        trophiesLookupListener.assertLookedUp(trophies);
    }

    @Test
    public void test_getAllTrophies_UnverifiedUser() {
        try {
            gameJolt.getAllTrophies(trophiesLookupListener);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getTrophy_Unverified() {
        try {
            gameJolt.getTrophy(12, trophyLookupListener);
            fail();
        } catch (UnverifiedUserException er) {

        }
    }

    @Test
    public void test_getTrophy_NoMatchingTrophy() {
        hasAVerifiedUser();
        whenWeQueryForTrophyByIdWithNoResult(12);

        gameJolt.getTrophy(12, trophyLookupListener);

        trophyLookupListener.assertNotFound();
    }

    @Test
    public void test_getTrophy() throws MalformedURLException {
        Trophy trophy = new Trophy();

        hasAVerifiedUser();
        whenWeQueryForTrophyById(12, trophy);

        gameJolt.getTrophy(12, trophyLookupListener);

        trophyLookupListener.assertFound(trophy);
    }

    @Test
    public void test_achievedTrophy_UnverifiedUser() {
        try {
            gameJolt.achievedTrophy(1234, trophyAchievedListener);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_achievedTrophy_VerifiedUser() {
        Trophy trophy = new Trophy();
        hasAVerifiedUser();
        whenUserAchievedATrophy(1234, true);
        whenWeQueryForTrophyById(1234, trophy);

        gameJolt.achievedTrophy(1234, trophyAchievedListener);

        trophyAchievedListener.assertAchieved(trophy);
    }

    @Test
    public void test_achievedTrophy_AlreadyAchieved() {
        hasAVerifiedUser();
        whenUserAchievedATrophy(1234, false);

        gameJolt.achievedTrophy(1234, trophyAchievedListener);

        trophyAchievedListener.assertNotAchieved();
    }

    @Test
    public void test_verifyUser_NotVerified() {
        whenUserFailsVerification(USERNAME, USER_TOKEN);

        gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);

        userVerificationListener.assertNotVerified();
    }

    @Test
    public void test_verifyUser_Verified() {
        whenUserPassesVerification(USERNAME, USER_TOKEN);

        gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);

        userVerificationListener.assertVerified();
    }

    @Test
    public void test_verifyUser_BadResponse() {
        whenUserVerificationGetsBadResponseCode(USERNAME, USER_TOKEN);

        try {
            gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);
            fail();
        } catch (GameJoltException err) {

        }
    }

    @Test
    public void test_verifyUser_AlreadyVerified() {
        whenUserPassesVerification(USERNAME, USER_TOKEN);

        gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);
        gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);

        verify(requestFactory, times(1)).buildVerifyUserRequest(USERNAME, USER_TOKEN);
    }

    @Test
    public void test_verifyUser_DifferentUsernameAndFails() {
        hasAVerifiedUser();
        whenUserFailsVerification("different", USER_TOKEN);

        gameJolt.verifyUser("different", USER_TOKEN, userVerificationListener);

        userVerificationListener.assertNotVerified();
    }

    @Test
    public void test_verifyUser_DifferentUserTokenAndFails() {
        hasAVerifiedUser();
        whenUserFailsVerification(USERNAME, "differentUserToken");

        gameJolt.verifyUser(USERNAME, "differentUserToken", userVerificationListener);

        userVerificationListener.assertNotVerified();
    }

    private void hasAVerifiedUser() {
        whenUserPassesVerification(USERNAME, USER_TOKEN);
        gameJolt.verifyUser(USERNAME, USER_TOKEN, userVerificationListener);
    }

    private Properties properties(boolean successful) {
        Properties properties = new Properties();
        properties.put("success", String.valueOf(successful));
        return properties;
    }

    private void whenWeExpectToQueryForAllUserDataKeysAndNoneAreFound() {
        whenWeExpectToQueryForAllUserDataKeys();
    }

    private void whenWeExpectToQueryForAllUserDataKeys(String... keys) {
        Properties properties = new Properties();
        for (String key : keys) {
            properties.put(key, key);
        }
        whenWeExpectToQueryForAllUserDataKeys(properties);
    }

    private void whenWeExpectToQueryForAllUserDataKeys(Properties properties) {
        MockHttpTuple tuple = new MockHttpTuple("user-data-keys");
        tuple.whenIsSuccessful();
        HttpRequest request = tuple.request;

        when(requestFactory.buildUserDataKeysRequest(USERNAME, USER_TOKEN)).thenReturn(request);
        ArrayList<String> keys = new ArrayList<String>(properties.asMap().keySet());
        when(propertiesParser.parseToList(tuple.responseContent, "key")).thenReturn(keys);
    }

    private void whenWeQueryForUserData(String key, Object expectedValue) {
        String dataContent = whenWeDeserializeTheData(key, expectedValue);
        MockHttpTuple tuple = new MockHttpTuple("user-data");
        tuple.whenIsSuccessfulWithResponse("Success\n" + dataContent);

        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, key)).thenReturn(tuple.request);
    }

    private void whenWeQueryForGameData(String key, Object value) {
        String storedData = whenWeDeserializeTheData(key, value);
        MockHttpTuple tuple = new MockHttpTuple("game-data");
        tuple.whenIsSuccessfulWithResponse("Success\r\n" + storedData);
        when(requestFactory.buildGetGameDataRequest(key)).thenReturn(tuple.request);
    }

    private void whenWeQueryForGameDataWithUnixLineEndings(String key, Object value) {
        String storedData = whenWeDeserializeTheData(key, value);
        MockHttpTuple tuple = new MockHttpTuple("game-data");
        tuple.whenIsSuccessfulWithResponse("Success\n" + storedData);
        when(requestFactory.buildGetGameDataRequest(key)).thenReturn(tuple.request);

    }

    private void whenWeQueryForGameDataAndItFails(String key) {
        MockHttpTuple tuple = new MockHttpTuple("game-data-failure");
        tuple.whenIsSuccessfulWithResponse("FAILURE\nerror message");
        when(requestFactory.buildGetGameDataRequest(key)).thenReturn(tuple.request);
    }

    private void whenWeQueryForUserDataAndItFails() {
        MockHttpTuple tuple = new MockHttpTuple("user-data-fails");
        tuple.whenIsSuccessfulWithResponse("FAILURE\nerror message");
        when(requestFactory.buildGetUserDataRequest(USERNAME, USER_TOKEN, "key-value")).thenReturn(tuple.request);
    }

    private void whenWeQueryForGameDataKeys(String... keys) {
        MockHttpTuple tuple = new MockHttpTuple("all-game-data-keys");
        tuple.whenIsSuccessful();
        when(requestFactory.buildGameDataKeysRequest()).thenReturn(tuple.request);
        when(propertiesParser.parseToList(tuple.responseContent, "key")).thenReturn(Arrays.asList(keys));
    }

    private void whenWeDeleteGameData(String name) {
        whenWeDeleteGameData(name, true);
    }

    private void whenWeDeleteGameData(String name, boolean successful) {
        MockHttpTuple tuple = new MockHttpTuple("delete-game-data");
        tuple.whenIsSuccessful();
        when(requestFactory.buildRemoveGameDataRequest(name)).thenReturn(tuple.request);

        Properties properties = properties(successful);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(properties);
    }

    private void whenUserFailsVerification(String username, String userToken) {
        whenAttemptUserVerification(username, userToken, false);
    }

    private void whenUserPassesVerification(String username, String userToken) {
        whenAttemptUserVerification(username, userToken, true);
    }

    private void whenAttemptUserVerification(String username, String userToken, boolean successfullyVerified) {
        Properties properties = properties(successfullyVerified);

        MockHttpTuple tuple = new MockHttpTuple("verification");
        tuple.whenIsSuccessful();
        when(requestFactory.buildVerifyUserRequest(username, userToken)).thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(properties);
    }

    private void whenUserVerificationGetsBadResponseCode(String username, String userToken) {
        MockHttpTuple tuple = new MockHttpTuple("verification");
        tuple.whenIsFailure();
        when(requestFactory.buildVerifyUserRequest(username, userToken)).thenReturn(tuple.request);
    }

    private void whenWeRemoveUserData(String key) {
        whenAttemptToDeleteUserData(key, true);
    }

    private void whenWeFailedToRemoveUserData(String key) {
        whenAttemptToDeleteUserData(key, false);
    }

    private void whenAttemptToDeleteUserData(String key, boolean successful) {
        MockHttpTuple tuple = new MockHttpTuple("remove-user-data");
        tuple.whenIsSuccessful();
        Properties properties = properties(successful);

        when(requestFactory.buildRemoveUserDataRequest(USERNAME, USER_TOKEN, key)).thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(properties);
    }

    private void whenWeStoreUserData(String key, Object value) {
        MockHttpTuple tuple = new MockHttpTuple("store-user-data");
        tuple.whenIsSuccessful();

        String sanitizedData = whenWeSerializeData(key, value);
        when(requestFactory.buildStoreUserDataRequest(USERNAME, USER_TOKEN, key, sanitizedData)).thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(successfulResponse());
    }

    private void whenStoreGameData(String key, Object obj) {
        MockHttpTuple tuple = new MockHttpTuple("store-game-data");
        tuple.whenIsSuccessful();

        String sanitizedData = whenWeSerializeData(key, obj);
        when(requestFactory.buildStoreGameDataRequest(key, sanitizedData)).thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(successfulResponse());
    }

    private String whenWeSerializeData(String prefix, Object obj) {
        String sanitizedData = prefix + "sanitized-data";
        byte[] data = (prefix + "unsanitized-data").getBytes();
        when(objectSerializer.serialize(obj)).thenReturn(data);
        when(binarySanitizer.sanitize(data)).thenReturn(sanitizedData);
        return sanitizedData;
    }

    private String whenWeDeserializeTheData(String key, Object value) {
        String storedData = key + "-data-stored";
        byte[] unsanitizedData = (key + "-unsanitized-data").getBytes();
        when(binarySanitizer.unsanitize(storedData)).thenReturn(unsanitizedData);
        when(objectSerializer.deserialize(unsanitizedData)).thenReturn(value);
        return storedData;
    }

    private void whenWeQueryForALimitedNumberOfHighscores(int expectedLimited, List<Highscore> parseScores) {
        MockHttpTuple tuple = new MockHttpTuple("limited-high-scores");
        tuple.whenIsSuccessful();
        when(requestFactory.buildAllHighscoresRequest(expectedLimited)).thenReturn(tuple.request);
        when(highscoreParser.parse(tuple.responseContent)).thenReturn(parseScores);
    }

    private void whenWeQueryForUserHighscores(int expectedLimit, List<Highscore> parseScores) {
        MockHttpTuple tuple = new MockHttpTuple("user-high-scores");
        tuple.whenIsSuccessful();
        when(requestFactory.buildUserHighscoresRequest(USERNAME, USER_TOKEN, expectedLimit)).thenReturn(tuple.request);
        when(highscoreParser.parse(tuple.responseContent)).thenReturn(parseScores);
    }

    private void whenUserAchievedATrophy(int trophyId, boolean successful) {
        MockHttpTuple tuple = new MockHttpTuple("achieved-trophy");
        tuple.whenIsSuccessful();
        when(requestFactory.buildAchievedTrophyRequest(USERNAME, USER_TOKEN, String.valueOf(trophyId))).thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(properties(successful));
    }

    private void whenWeQueryForTrophyByIdWithNoResult(int trophyId) {
        whenWeAttemptToQueryForATrophyById(trophyId, Optional.<Trophy>absent());
    }

    private void whenWeQueryForTrophyById(int trophyId, Trophy trophy) {
        whenWeAttemptToQueryForATrophyById(trophyId, Optional.fromNullable(trophy));
    }

    private void whenWeAttemptToQueryForATrophyById(int trophyId, Optional<Trophy> optional) {
        MockHttpTuple tuple = new MockHttpTuple("trophy-by-id");
        tuple.whenIsSuccessful();

        when(requestFactory.buildTrophyRequest(USERNAME, USER_TOKEN, String.valueOf(trophyId))).thenReturn(tuple.request);
        ArrayList<Trophy> trophies = new ArrayList<Trophy>();
        if (optional.isPresent()) {
            trophies.add(optional.get());
        }
        when(trophyParser.parse(tuple.responseContent)).thenReturn(trophies);
    }

    private void whenUserHasAchievedAHighScoreSuccessfully() {
        whenAttemptToStoreAHighScore(true);
    }

    private void whenAttemptToStoreAHighScore(boolean successful) {
        MockHttpTuple tuple = new MockHttpTuple("achieved-high-score");
        tuple.whenIsSuccessful();
        when(requestFactory.buildUserAchievedHighscoreRequest(eq(USERNAME), eq(USER_TOKEN), anyString(), anyInt(), anyString()))
                .thenReturn(tuple.request);
        when(propertiesParser.parseProperties(tuple.responseContent)).thenReturn(properties(successful));
    }

    private void assertHighScoreWasPassedServer(String displayedText, int score, String extra) {
        verify(requestFactory).buildUserAchievedHighscoreRequest(
                USERNAME, USER_TOKEN, displayedText, score, extra
        );
    }

    private void whenWeRequestForUnachievedTrophies(List trophies) {
        whenWeRequestForTrophies("false", trophies);
    }

    private void whenWeRequestForAchievedTrophies(List trophies) {
        whenWeRequestForTrophies("true", trophies);
    }

    private void whenWeRequestForAllTrophies(List trophies) {
        whenWeRequestForTrophies("empty", trophies);
    }

    private void whenWeRequestForTrophies(String type, List trophies) {
        MockHttpTuple tuple = new MockHttpTuple("all-trophies");
        tuple.whenIsSuccessful();
        when(requestFactory.buildTrophiesRequest(USERNAME, USER_TOKEN, type)).thenReturn(tuple.request);
        when(trophyParser.parse(tuple.responseContent)).thenReturn(trophies);
    }

    private Properties successfulResponse() {
        return properties(true);
    }

    private class MockHttpTuple {
        HttpRequest request;
        String responseContent;
        private String prefix;

        MockHttpTuple(String prefix) {
            this.prefix = prefix;
        }

        void whenIsSuccessful() {
            whenIsSuccessfulWithResponse(prefix += "-response-content");
        }

        void whenIsSuccessfulWithResponse(String responseContent) {
            this.responseContent = responseContent;
            this.request = new MockHttpRequest(true, responseContent);
        }

        void whenIsFailure() {
            this.request = new MockHttpRequest(false, responseContent);
        }
    }
}
