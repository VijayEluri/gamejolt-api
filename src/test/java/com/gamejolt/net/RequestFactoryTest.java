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
    private static final String PLAYER = "player";
    private static final String USER_TOKEN = "userToken";
    private Map<String, String> parameters;
    private static final String PRIVATE_KEY = "private-key";

    @Before
    public void setUp() throws Exception {
        signatureFactory = mock(SignatureFactory.class);

        factory = new RequestFactory(GAME_ID, PRIVATE_KEY, signatureFactory);

        parameters = new LinkedHashMap<String, String>();
        parameters.put("game_id", String.valueOf(GAME_ID));
        parameters.put("username", PLAYER);
    }

    @Test
    public void test_buildVerifyUserRequest_DifferentVersion() {
        factory.setVersion("2.0");
        when(signatureFactory.build("http://gamejolt.com/api/game/v2.0/users/auth/", parameters, USER_TOKEN)).thenReturn("sign-hash");

        HttpRequest request = factory.buildVerifyUserRequest(PLAYER, USER_TOKEN);

        assertEquals("http://gamejolt.com/api/game/v2.0/users/auth/?game_id=1111&username=player&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildVerifyUserRequest() {
        when(signatureFactory.build("http://gamejolt.com/api/game/v1/users/auth/", parameters, USER_TOKEN)).thenReturn("sign-hash");

        HttpRequest request = factory.buildVerifyUserRequest(PLAYER, USER_TOKEN);

        assertEquals("http://gamejolt.com/api/game/v1/users/auth/?game_id=1111&username=player&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildAchievedTrophyRequest() {
        parameters.put("trophy_id", "trophy1");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/add-achieved", parameters, USER_TOKEN)).thenReturn("sign-hash");

        HttpRequest request = factory.buildAchievedTrophyRequest(PLAYER, USER_TOKEN, "trophy1");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/add-achieved?game_id=1111&username=player&trophy_id=trophy1&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildTrophyRequest() {
        parameters.put("trophy_id", "trophy1");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/", parameters, USER_TOKEN)).thenReturn("sign-hash");

        HttpRequest request = factory.buildTrophyRequest(PLAYER, USER_TOKEN, "trophy1");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/?game_id=1111&username=player&trophy_id=trophy1&signature=sign-hash&user_token=userToken", request.getUrl());
    }

    @Test
    public void test_buildTrophiesRequest() {
        parameters.put("achieved", "empty");

        when(signatureFactory.build("http://gamejolt.com/api/game/v1/trophies/", parameters, USER_TOKEN)).thenReturn("sign-hash");

        HttpRequest request = factory.buildTrophiesRequest(PLAYER, USER_TOKEN, "empty");

        assertEquals("http://gamejolt.com/api/game/v1/trophies/?game_id=1111&username=player&achieved=empty&signature=sign-hash&user_token=userToken", request.getUrl());
    }
}
