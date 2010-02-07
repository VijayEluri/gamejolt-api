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
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class PropertiesParserTest {
    private PropertiesParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new PropertiesParser();
    }

    @Test
    public void test_parse_singleProperty_MultipleTimes() {
        List<Properties> properties = parser.parse("success: \"true\"\nsuccess:\"false\"");

        assertEquals(2, properties.size());
        assertEquals("true", properties.get(0).get("success"));
        assertEquals("false", properties.get(1).get("success"));
    }

    @Test
    public void test_parse_singleProperty() {
        List<Properties> properties = parser.parse("success: \"true\"");

        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertEquals("true", properties.get(0).get("success"));
    }

    @Test
    public void test_parseProperties_singleProperty() {
        Properties properties = parser.parseProperties("success: \"true\"");

        assertNotNull(properties);
        assertEquals("true", properties.get("success"));
    }

    @Test
    public void test_parseProperties_noProperties() {
        Properties properties = parser.parseProperties("");

        assertNotNull(properties);
        assertEquals(0, properties.asMap().size());
    }

    @Test
    public void test_parseProperties_singleProperty_MultipleTimes() {
        try {
            parser.parseProperties("success: \"true\"\nsuccess:\"false\"");
            fail();
        } catch (GameJoltException e) {
            assertEquals("Not a single instance of properties found (2 instances)", e.getMessage());
        }
    }
}
