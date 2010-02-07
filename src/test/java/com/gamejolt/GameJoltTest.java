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
import com.gamejolt.net.RequestFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GameJoltTest {
    private RequestFactory requestFactory;
    private HttpRequest request;
    private HttpResponse response;
    private GameJolt gameJolt;

    @Before
    public void setUp() throws Exception {
        requestFactory = mock(RequestFactory.class);
        request = mock(HttpRequest.class);
        response = mock(HttpResponse.class);

        gameJolt = new GameJolt(1111, "private-key");
        gameJolt.setRequestFactory(requestFactory);

        when(request.doGet()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
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
        when(response.getContentAsString()).thenReturn("success:\"true\"");

        assertTrue(gameJolt.achievedTrophy(1234));
    }

    @Test
    public void test_verifyUser_NotVerified() {
        when(requestFactory.buildVerifyUserRequest("username", "userToken")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("success:\"true\"");

        assertTrue(gameJolt.verifyUser("username", "userToken"));
    }

    @Test
    public void test_verifyUser_Verified() {
        when(requestFactory.buildVerifyUserRequest("username", "userToken")).thenReturn(request);
        when(response.getContentAsString()).thenReturn("success:\"true\"");

        assertTrue(gameJolt.verifyUser("username", "userToken"));
    }

    private void hasAVerifiedUser(String username, String userToken) {
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(requestFactory.buildVerifyUserRequest(username, userToken)).thenReturn(httpRequest);
        when(httpRequest.doGet()).thenReturn(httpResponse);
        when(httpResponse.isSuccessful()).thenReturn(true);
        when(httpResponse.getContentAsString()).thenReturn("success:\"true\"");

        assertTrue(gameJolt.verifyUser(username, userToken));
    }
}
