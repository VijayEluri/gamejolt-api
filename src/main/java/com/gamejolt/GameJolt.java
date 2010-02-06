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
    private int gameId;
    private String privateKey;
    private RequestFactory requestFactory;

    public GameJolt(int gameId, String privateKey) {
        this.gameId = gameId;
        this.privateKey = privateKey;
        this.requestFactory = new RequestFactory(gameId, this.privateKey);
    }

    public boolean verifyUser(String username, String userToken) {
        HttpRequest request = requestFactory.buildVerifyUserRequest(username, userToken);
        HttpResponse response = request.doGet();
        Properties properties = new Properties(response.getContentAsString());
        return properties.getBoolean("success");
    }

    protected void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }
}
