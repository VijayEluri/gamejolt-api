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

import com.gamejolt.Highscore;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HighscoreParserTest {
    private PropertiesParser propertiesParser;
    private HighscoreParser parser;
    private static final String CONTENT = "content";
    private Properties properties;

    @Before
    public void setUp() throws Exception {
        propertiesParser = mock(PropertiesParser.class);
        parser = new HighscoreParser(propertiesParser);
        properties = mock(Properties.class);
        when(properties.getBoolean("success")).thenReturn(true);
    }

    @Test
    public void test_failed_request() {
        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        when(properties.getBoolean("success")).thenReturn(false);

        List<Highscore> scores = parser.parse(CONTENT);

        assertEquals(0, scores.size());
    }

    @Test
    public void test_one_GuestHighscore() {
        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        when(properties.isBlank("user")).thenReturn(true);
        when(properties.get("user")).thenReturn("");
        when(properties.getInt("user_id")).thenReturn(0);
        when(properties.get("guest")).thenReturn("guest-12");

        List<Highscore> scores = parser.parse(CONTENT);

        assertEquals(1, scores.size());

        Highscore score = scores.get(0);
        assertEquals("guest-12", score.getUser());
        assertEquals(0, score.getUserId());
        assertTrue(score.isGuestScore());
    }

    @Test
    public void test_one_NonGuestHighscore() {
        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties));

        expectHighScore(properties);

        List<Highscore> scores = parser.parse(CONTENT);

        assertNotNull(scores);
        assertEquals(1, scores.size());

        Highscore score = scores.get(0);
        assertEquals("score-data", score.getDisplayedScore());
        assertEquals(2, score.getScore());
        assertEquals("extra-data", score.getExtraData());
        assertEquals("username", score.getUser());
        assertEquals(1, score.getUserId());
        assertFalse(score.isGuestScore());
        assertEquals("date", score.getTimeOfScore());
    }

    @Test
    public void test_multiple_Highscores() {
        Properties otherProperties = mock(Properties.class);

        when(propertiesParser.parse(CONTENT)).thenReturn(Arrays.asList(properties, otherProperties));

        expectHighScore(properties);
        expectHighScore(otherProperties);

        List<Highscore> scores = parser.parse(CONTENT);

        assertEquals(2, scores.size());
    }

    private void expectHighScore(Properties properties) {
        when(properties.isBlank("user")).thenReturn(false);
        when(properties.get("score")).thenReturn("score-data");
        when(properties.getInt("sort")).thenReturn(2);
        when(properties.get("extra_data")).thenReturn("extra-data");
        when(properties.get("user")).thenReturn("username");
        when(properties.getInt("user_id")).thenReturn(1);
        when(properties.get("guest")).thenReturn("");
        when(properties.get("stored")).thenReturn("date");
    }


}
