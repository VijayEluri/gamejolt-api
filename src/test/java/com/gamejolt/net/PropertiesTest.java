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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;


public class PropertiesTest {
    @Test
    public void test_StringValue() {
        Properties properties = new Properties("message:\"The signature you entered for the request is invalid.\"");

        assertEquals("The signature you entered for the request is invalid.", properties.get("message"));
    }

    @Test
    public void test_BadUrlValue() throws MalformedURLException {
        Properties properties = new Properties("value:\"x\"");

        try {
            properties.getUrl("value");
            fail();
        } catch (GameJoltException e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void test_UrlValue() throws MalformedURLException {
        Properties properties = new Properties("value:\"http://www.google.com\"");

        assertEquals(new URL("http://www.google.com"), properties.getUrl("value"));
    }

    @Test
    public void test_IntValue() {
        Properties properties = new Properties("value:\"1\"");

        assertEquals(1, properties.getInt("value"));
    }

    @Test
    public void test_BooleanValue_False() {
        Properties properties = new Properties("success:\"false\"");

        assertFalse(properties.getBoolean("success"));
    }

    @Test
    public void test_BooleanValue_True() {
        Properties properties = new Properties("success:\"true\"");

        assertTrue(properties.getBoolean("success"));
    }

    @Test
    public void test_MultipleValues_UnixLineEndings() {
        Properties properties = new Properties("success:\"true\"\nkey:\"value\"");

        Map<String, String> map = properties.asMap();

        assertEquals(2, map.size());
        assertEquals("true", map.get("success"));
        assertEquals("value", map.get("key"));
    }

    @Test
    public void test_MultipleValues_WindowsLineEndings() {
        Properties properties = new Properties("success:\"true\"\r\nkey:\"value\"");

        Map<String, String> map = properties.asMap();

        assertEquals(2, map.size());
        assertEquals("true", map.get("success"));
        assertEquals("value", map.get("key"));
    }
}
