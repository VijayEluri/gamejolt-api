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
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GameJoltTest {
    private RequestFactory requestFactory;
    private HttpRequest request;
    private HttpResponse response;
    private GameJolt gameJolt;
    private TrophyResponseParser trophyResponseParser;
    private PropertiesParser propertiesParser;

    @Before
    public void setUp() throws Exception {
        requestFactory = mock(RequestFactory.class);
        request = mock(HttpRequest.class);
        response = mock(HttpResponse.class);
        trophyResponseParser = mock(TrophyResponseParser.class);
        propertiesParser = mock(PropertiesParser.class);

        gameJolt = new GameJolt(1111, "private-key");
        gameJolt.setRequestFactory(requestFactory);
        gameJolt.setTrophyParser(trophyResponseParser);
        gameJolt.setPropertiesParser(propertiesParser);

        when(request.doGet()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
    }

    @Test
    public void test_getUnachievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getUnachievedTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getUnachievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildTrophiesRequest("username", "userToken", "false")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("content");
        when(trophyResponseParser.parse("content")).thenReturn(trophies);

        assertSame(trophies, gameJolt.getUnachievedTrophies());
    }

    @Test
    public void test_getAchievedTrophies_UnverifiedUser() {
        try {
            gameJolt.getAchievedTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getAchievedTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildTrophiesRequest("username", "userToken", "true")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("content");
        when(trophyResponseParser.parse("content")).thenReturn(trophies);

        assertSame(trophies, gameJolt.getAchievedTrophies());
    }

    @Test
    public void test_getAllTrophies() {
        List trophies = new ArrayList();

        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildTrophiesRequest("username", "userToken", "empty")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("content");
        when(trophyResponseParser.parse("content")).thenReturn(trophies);

        assertSame(trophies, gameJolt.getAllTrophies());
    }

    @Test
    public void test_getAllTrophies_UnverifiedUser() {
        try {
            gameJolt.getAllTrophies();
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_getTrophy_Unverified() {
        try {
            gameJolt.getTrophy(12);
            fail();
        } catch (UnverifiedUserException er) {

        }
    }

    @Test
    public void test_getTrophy_NoMatchingTrophy() {
        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildTrophyRequest("username", "userToken", "12")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("content");
        when(trophyResponseParser.parse("content")).thenReturn(new ArrayList());

        assertNull(gameJolt.getTrophy(12));
    }

    @Test
    public void test_getTrophy() throws MalformedURLException {
        Trophy trophy = new Trophy(0, null, null, null, null, null);

        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildTrophyRequest("username", "userToken", "12")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("content");
        when(trophyResponseParser.parse("content")).thenReturn(Arrays.asList(trophy));

        assertSame(trophy, gameJolt.getTrophy(12));
    }

    @Test
    public void test_achievedTrophy_UnverifiedUser() {
        try {
            gameJolt.achievedTrophy(1234);
            fail();
        } catch (UnverifiedUserException e) {

        }
    }

    @Test
    public void test_achievedTrophy_VerifiedUser() {
        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildAchievedTrophyRequest("username", "userToken", "1234")).thenReturn(request);
        receivesSuccessfulResponse(response, true);

        assertTrue(gameJolt.achievedTrophy(1234));
    }

    @Test
    public void test_achievedTrophy_AlreadyAchieved() {
        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildAchievedTrophyRequest("username", "userToken", "1234")).thenReturn(request);
        receivesSuccessfulResponse(response, false);

        assertFalse(gameJolt.achievedTrophy(1234));
    }

    @Test
    public void test_verifyUser_NotVerified() {
        when(requestFactory.buildVerifyUserRequest("username", "userToken")).thenReturn(request);
        receivesSuccessfulResponse(response, false);

        assertFalse(gameJolt.verifyUser("username", "userToken"));
    }

    @Test
    public void test_verifyUser_Verified() {
        when(requestFactory.buildVerifyUserRequest("username", "userToken")).thenReturn(request);
        receivesSuccessfulResponse(response, true);

        assertTrue(gameJolt.verifyUser("username", "userToken"));
    }

    @Test
    public void test_verifyUser_AlreadyVerified() {
        when(requestFactory.buildVerifyUserRequest("username", "userToken")).thenReturn(request);
        receivesSuccessfulResponse(response, true);

        assertTrue(gameJolt.verifyUser("username", "userToken"));
        assertTrue(gameJolt.verifyUser("username", "userToken"));

        verify(requestFactory, times(1)).buildVerifyUserRequest("username", "userToken");
    }

    @Test
    public void test_verifyUser_DifferentUserAndFails() {
        hasAVerifiedUser("username", "userToken");

        when(requestFactory.buildVerifyUserRequest("different", "differentUserToken")).thenReturn(request);
        receivesSuccessfulResponse(response, false);

        assertFalse(gameJolt.verifyUser("different", "differentUserToken"));
    }

    private void hasAVerifiedUser(String username, String userToken) {
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(requestFactory.buildVerifyUserRequest(username, userToken)).thenReturn(httpRequest);
        when(httpRequest.doGet()).thenReturn(httpResponse);
        when(httpResponse.isSuccessful()).thenReturn(true);
        receivesSuccessfulResponse(httpResponse, true);

        assertTrue(gameJolt.verifyUser(username, userToken));
    }

    private void receivesSuccessfulResponse(HttpResponse httpResponse, boolean successful) {
        when(httpResponse.getContentAsString()).thenReturn("content");
        Properties properties = new Properties();
        properties.put("success", String.valueOf(successful));
        when(propertiesParser.parseProperties("content")).thenReturn(properties);
    }
}
