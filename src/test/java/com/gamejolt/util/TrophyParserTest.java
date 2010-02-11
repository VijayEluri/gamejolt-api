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

import com.gamejolt.Trophy;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.gamejolt.Trophy.Difficulty.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TrophyParserTest {
    private TrophyParser parser;
    private PropertiesParser propertiesParser;
    private static final String CONTENT = "content";

    @Before
    public void setUp() throws Exception {
        propertiesParser = mock(PropertiesParser.class);

        parser = new TrophyParser(propertiesParser);
    }

    @Test
    public void test_multipleTrophies() throws MalformedURLException {
        Properties trophy1 = new Properties();
        trophy1.put("id", "187");
        trophy1.put("title", "test");
        trophy1.put("description", "test");
        trophy1.put("difficulty", "Bronze");
        trophy1.put("image_url", "http://gamejolt.com/img/trophy-bronze-1.jpg");
        trophy1.put("achieved", "14 hours ago");

        Properties trophy2 = new Properties();
        trophy2.put("id", "188");
        trophy2.put("title", "test silver");
        trophy2.put("description", "ta da, da da");
        trophy2.put("difficulty", "Silver");
        trophy2.put("image_url", "http://gamejolt.com/img/trophy-silver-1.jpg");
        trophy2.put("achieved", "false");

        Properties trophy3 = new Properties();
        trophy3.put("id", "189");
        trophy3.put("title", "test platinum");
        trophy3.put("description", "ta da, da da");
        trophy3.put("difficulty", "Platinum");
        trophy3.put("image_url", "http://gamejolt.com/img/trophy-platinum-1.jpg");
        trophy3.put("achieved", "false");

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(trophy1, trophy2, trophy3));

        List<Trophy> trophies = parser.parse(CONTENT);

        assertEquals(3, trophies.size());
        assertTrophy(187, "test", "test", BRONZE, new URL("http://gamejolt.com/img/trophy-bronze-1.jpg"), "14 hours ago", trophies.get(0));
        assertTrophy(188, "test silver", "ta da, da da", SILVER, new URL("http://gamejolt.com/img/trophy-silver-1.jpg"), "", trophies.get(1));
        assertTrophy(189, "test platinum", "ta da, da da", PLATINUM, new URL("http://gamejolt.com/img/trophy-platinum-1.jpg"), "", trophies.get(2));
    }

    @Test
    public void test_singleTrophy() throws MalformedURLException {
        Properties properties = new Properties();
        properties.put("id", "187");
        properties.put("title", "test");
        properties.put("description", "test");
        properties.put("difficulty", "Bronze");
        properties.put("image_url", "http://gamejolt.com/img/trophy-bronze-1.jpg");
        properties.put("achieved", "3 hours ago");

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        List<Trophy> trophies = parser.parse(CONTENT);

        assertNotNull(trophies);
        assertEquals(1, trophies.size());
        assertTrophy(187, "test", "test", BRONZE, new URL("http://gamejolt.com/img/trophy-bronze-1.jpg"), "3 hours ago", trophies.get(0));
    }

    @Test
    public void test_singleTrophy_Achieved() throws MalformedURLException {
        Properties properties = new Properties();
        properties.put("id", "187");
        properties.put("difficulty", "Bronze");
        properties.put("image_url", "http://gamejolt.com/img/trophy-bronze-1.jpg");
        properties.put("achieved", "3 hours ago");

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        Trophy trophy = parser.parse(CONTENT).get(0);
        assertTrue(trophy.isAchieved());
        assertEquals("3 hours ago", trophy.getTime());
    }

    @Test
    public void test_singleTrophy_NotAchieved() throws MalformedURLException {
        Properties properties = new Properties();
        properties.put("id", "187");
        properties.put("difficulty", "Bronze");
        properties.put("image_url", "http://gamejolt.com/img/trophy-bronze-1.jpg");
        properties.put("achieved", "false");

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        Trophy trophy = parser.parse(CONTENT).get(0);
        assertFalse(trophy.isAchieved());
        assertEquals("", trophy.getTime());
    }

    @Test
    public void test_noMatchingTrophy() throws MalformedURLException {
        Properties properties = new Properties();
        properties.put("id", "0");

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        List<Trophy> trophies = parser.parse(CONTENT);

        assertEquals(0, trophies.size());
    }

    private void assertTrophy(int id, String title, String description, Trophy.Difficulty difficulty, URL image, String achieved, Trophy actualTrophy) {
        assertEquals(id, actualTrophy.getId());
        assertEquals(title, actualTrophy.getTitle());
        assertEquals(description, actualTrophy.getDescription());
        assertEquals(difficulty, actualTrophy.getDifficulty());
        assertEquals(image, actualTrophy.getImageUrl());
        assertEquals(achieved, actualTrophy.getTime());
    }

}
