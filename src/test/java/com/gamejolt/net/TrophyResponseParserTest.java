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

import com.gamejolt.Trophy;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.gamejolt.Trophy.Difficulty.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class TrophyResponseParserTest {
    @Test
    public void test_multipleTrophies() throws MalformedURLException {
        TrophyResponseParser parser = new TrophyResponseParser();

        List<Trophy> trophies = parser.parse("success:\"true\"\n" +
                "id:\"187\"\n" +
                "title:\"test\"\n" +
                "description:\"test\"\n" +
                "difficulty:\"Bronze\"\n" +
                "image_url:\"http://gamejolt.com/img/trophy-bronze-1.jpg\"\n" +
                "achieved:\"14 hours ago\"\n" +
                "id:\"188\"\n" +
                "title:\"test silver\"\n" +
                "description:\"ta da, da da\"\n" +
                "difficulty:\"Silver\"\n" +
                "image_url:\"http://gamejolt.com/img/trophy-silver-1.jpg\"\n" +
                "achieved:\"false\"\n" +
                "id:\"189\"\n" +
                "title:\"test platinum\"\n" +
                "description:\"ta da, da da\"\n" +
                "difficulty:\"Platinum\"\n" +
                "image_url:\"http://gamejolt.com/img/trophy-platinum-1.jpg\"\n" +
                "achieved:\"false\"");

        assertEquals(3, trophies.size());
        assertTrophy(187, "test", "test", BRONZE, new URL("http://gamejolt.com/img/trophy-bronze-1.jpg"), "14 hours ago", trophies.get(0));
        assertTrophy(188, "test silver", "ta da, da da", SILVER, new URL("http://gamejolt.com/img/trophy-silver-1.jpg"), "false", trophies.get(1));
        assertTrophy(189, "test platinum", "ta da, da da", PLATINUM, new URL("http://gamejolt.com/img/trophy-platinum-1.jpg"), "false", trophies.get(2));
    }

    @Test
    public void test_singleTrophy() throws MalformedURLException {
        TrophyResponseParser parser = new TrophyResponseParser();

        List<Trophy> trophies = parser.parse("success:\"true\"\n" +
                "id:\"187\"\n" +
                "title:\"test\"\n" +
                "description:\"test\"\n" +
                "difficulty:\"Bronze\"\n" +
                "image_url:\"http://gamejolt.com/img/trophy-bronze-1.jpg\"\n" +
                "achieved:\"3 hours ago\"");

        assertNotNull(trophies);
        assertEquals(1, trophies.size());
        assertTrophy(187, "test", "test", BRONZE, new URL("http://gamejolt.com/img/trophy-bronze-1.jpg"), "3 hours ago", trophies.get(0));
    }

    @Test
    public void test_noMatchingTrophy() throws MalformedURLException {
        TrophyResponseParser parser = new TrophyResponseParser();

        List<Trophy> trophies = parser.parse("success:\"true\"\n" +
                "id:\"0\"\n" +
                "title:\"\"\n" +
                "description:\"\"\n" +
                "difficulty:\"\"\n" +
                "image_url:\"\"\n" +
                "achieved:\"\"");

        assertEquals(0, trophies.size());
    }

    private void assertTrophy(int id, String title, String description, Trophy.Difficulty difficulty, URL image, String achieved, Trophy actualTrophy) {
        assertEquals(id, actualTrophy.id);
        assertEquals(title, actualTrophy.title);
        assertEquals(description, actualTrophy.description);
        assertEquals(difficulty, actualTrophy.difficulty);
        assertEquals(image, actualTrophy.imageUrl);
        assertEquals(achieved, actualTrophy.timeOfAchievement);
    }
}
