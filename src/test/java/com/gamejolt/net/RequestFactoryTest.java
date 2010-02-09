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

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RequestFactoryTest {
    private SignatureFactory signatureFactory;
    private RequestFactory factory;
    private static final int GAME_ID = 1111;
    private static final String PLAYER = "username";
    private static final String USER_TOKEN = "userToken";
    private static final String PRIVATE_KEY = "private-key";

    @Before
    public void setUp() throws Exception {
        signatureFactory = mock(SignatureFactory.class);

        factory = new RequestFactory(GAME_ID, PRIVATE_KEY, signatureFactory);
    }

    @Test
    public void test_buildRemoveUserDataRequest() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);
        signatureParameters.put("key", "name");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/data-store/remove", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildRemoveUserDataRequest("username", "userToken", "name");

        assertEquals("http://gamejolt.com/api/game/v1/data-store/remove?game_id=1111&username=username&key=name&user_token=userToken&signature=sign-hash", request.getUrl());
    }

    @Test
    public void test_buildVerifyUserRequest_DifferentVersion() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);

        factory.setVersion("2.0");
        when(signatureFactory.build("http://gamejolt.com/api/game/v2.0/users/auth/", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildVerifyUserRequest(PLAYER, USER_TOKEN);

        assertEquals("http://gamejolt.com/api/game/v2.0/users/auth/?game_id=1111&username=username&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildVerifyUserRequest() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/users/auth/", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildVerifyUserRequest(PLAYER, USER_TOKEN);

        assertEquals("http://gamejolt.com/api/game/v1/users/auth/?game_id=1111&username=username&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildAchievedTrophyRequest() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);
        signatureParameters.put("trophy_id", "trophy1");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/add-achieved", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildAchievedTrophyRequest(PLAYER, USER_TOKEN, "trophy1");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/add-achieved?game_id=1111&username=username&trophy_id=trophy1&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildTrophyRequest() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);
        signatureParameters.put("trophy_id", "trophy1");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildTrophyRequest(PLAYER, USER_TOKEN, "trophy1");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/?game_id=1111&username=username&trophy_id=trophy1&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildTrophiesRequest() {
        Map<String, String> signatureParameters = new LinkedHashMap<String, String>();
        signatureParameters.put("game_id", String.valueOf(GAME_ID));
        signatureParameters.put("username", "username");
        signatureParameters.put("user_token", "userToken" + PRIVATE_KEY);
        signatureParameters.put("achieved", "empty");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/", signatureParameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildTrophiesRequest(PLAYER, USER_TOKEN, "empty");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/?game_id=1111&username=username&achieved=empty&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildStoreGameDataRequest() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("game_id", String.valueOf(GAME_ID));
        parameters.put("data", "data");
        parameters.put("key", "name" + PRIVATE_KEY);

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/data-store/set", parameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildStoreGameDataRequest("name", "data");

        assertEquals("http://gamejolt.com/api/game/v1/data-store/set?game_id=1111&data=data&key=name&signature=sign-hash", request.getUrl());
    }

    @Test
    public void test_buildStoreUserDataRequest() {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("game_id", String.valueOf(GAME_ID));
        parameters.put("username", "username");
        parameters.put("data", "data");
        parameters.put("key", "name");
        parameters.put("user_token", "userToken" + PRIVATE_KEY);

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/data-store/set", parameters)).thenReturn("sign-hash");

        HttpRequest request = factory.buildStoreUserDataRequest("username", "userToken", "name", "data");

        assertEquals("http://gamejolt.com/api/game/v1/data-store/set?game_id=1111&username=username&data=data&key=name&user_token=userToken&signature=sign-hash", request.getUrl());
    }
}
