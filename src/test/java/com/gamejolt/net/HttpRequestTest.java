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

import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;


@Ignore
public class HttpRequestTest {

    @Test
    public void test_NoParameter() {
        HttpRequest request = new HttpRequest("http://www.google.com");

        assertEquals("http://www.google.com", request.getUrl());

        assertNotNull(request.execute(false));
    }

    @Test
    public void test_WithParameters() {
        HttpRequest request = new HttpRequest("http://www.bing.com/search");
        request.addParameter("q", "java").addParameter("form", "QBLH").addParameter("go", "").addParameter("qs", "n");

        assertNotNull(request.execute(false));
    }

    @Test
    public void test_WithParameters_EnsureParametersAreEncoded() {
        HttpRequest request = new HttpRequest("http://www.bing.com/search");
        request.addParameter("q", "java rest api").addParameter("form", "QBLH").addParameter("go", "").addParameter("qs", "n");

        assertNotNull(request.execute(false));
    }

    @Test
    public void test_BadUrl() {
        HttpRequest request = new HttpRequest("http://www.google.com/doesNotExist");
        request.addParameter("q", "java");

        try {
            request.execute(false);
            fail();
        } catch (HttpRequestException e) {
            assertEquals("Bad Http Response received response code 404", e.getMessage());
        }
    }

    @Test
    public void test_addParameters() {
        HttpRequest request = new HttpRequest("http://www.google.com");

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");

        request.addParameters(parameters);

        assertEquals("http://www.google.com?key1=value1&key2=value2", request.getUrl());

    }

}
