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

import com.gamejolt.GameJoltException;
import org.junit.Test;

import static org.junit.Assert.*;


public class RequestTest {
    @Test
    public void test_InValidUrl() {
        Request request = new Request("http://www.doesNotExistShouldThrowAHorribleError.com");

        try {
            request.doGet();
            fail();
        } catch (GameJoltException err) {

        }
    }

    @Test
    public void test_NoParameter() {
        Request request = new Request("http://www.google.com");

        Response response = request.doGet();

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertEquals(200, response.code);
    }

    @Test
    public void test_WithParameters() {
        Request request = new Request("http://www.bing.com/search");
        request.addParameter("q", "java").addParameter("form", "QBLH").addParameter("go", "").addParameter("qs", "n");

        Response response = request.doGet();

        assertNotNull(response.getContent());
        assertEquals(200, response.code);
    }

    @Test
    public void test_WithParameters_EnsureParametersAreEncoded() {
        Request request = new Request("http://www.bing.com/search");
        request.addParameter("q", "java rest api").addParameter("form", "QBLH").addParameter("go", "").addParameter("qs", "n");

        Response response = request.doGet();

        assertNotNull(response.getContent());
        assertEquals(200, response.code);
    }

    @Test
    public void test_BadUrl() {
        Request request = new Request("http://www.google.com");
        request.addParameter("q", "java");

        Response response = request.doGet();

        assertEquals(0, response.getContent().length);
        assertEquals(404, response.code);
    }


}
