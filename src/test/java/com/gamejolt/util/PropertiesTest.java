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

package com.gamejolt.util;

import com.gamejolt.GameJoltException;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;


public class PropertiesTest {
    private Properties properties;

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        properties.put("key", "value");
    }

    @Test
    public void ensureMapIsReadOnly() {
        try {
            properties.asMap().put("key", "value");
            fail();
        } catch (UnsupportedOperationException err) {
        }
    }

    @Test
    public void test_contains() {
        assertTrue(properties.contains("key"));
        assertFalse(properties.contains("doesNotExist"));
    }

    @Test
    public void test_BadUrlValue() throws MalformedURLException {
        try {
            properties.getUrl("key");
            fail();
        } catch (GameJoltException e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void test_UrlValue() throws MalformedURLException {
        properties.put("value", "http://www.google.com");

        assertEquals(new URL("http://www.google.com"), properties.getUrl("value"));
    }

    @Test
    public void test_IntValue() {
        properties.put("value", "1");

        assertEquals(1, properties.getInt("value"));
    }

    @Test
    public void test_BooleanValue_False() {
        properties.put("success", "false");

        assertFalse(properties.getBoolean("success"));
    }

    @Test
    public void test_BooleanValue_True() {
        properties.put("success", "true");

        assertTrue(properties.getBoolean("success"));
    }

}
