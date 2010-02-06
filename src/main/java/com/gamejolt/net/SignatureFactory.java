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

import com.gamejolt.util.Checksum;


public class SignatureFactory {
    private Checksum checksum = new Checksum();
    private String privateKey;

    public SignatureFactory(String privateKey) {
        this.privateKey = privateKey;
    }

    public String build(String baseUrl, int gameId, String username, String userToken) {
        HttpRequest request = new HttpRequest(baseUrl);
        request.addParameter("game_id", gameId);
        request.addParameter("username", username);
        request.addParameter("user_token", userToken + privateKey);
        return checksum.md5(request.getUrl());
    }

    protected void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }
}