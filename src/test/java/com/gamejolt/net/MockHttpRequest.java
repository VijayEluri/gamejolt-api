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

import java.util.Map;

public class MockHttpRequest implements HttpRequest {
    private final HttpResponse response;

    public MockHttpRequest(boolean success, String content) {
        response = new MockHttpResponse(success, content);
    }

    @Override
    public HttpRequest addParameter(String name, String value) {
        return this;
    }

    @Override
    public void addParameters(Map<String, String> parameters) {

    }

    @Override
    public void execute(HttpResponseHandler handler) {
        if (!response.isSuccessful()) {
            throw new HttpRequestException("Error");
        }

        handler.handle(response);
    }

    @Override
    public String getUrl() {
        return null;
    }
}
