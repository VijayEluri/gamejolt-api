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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TrophyParser {
    private PropertiesParser parser;

    public TrophyParser() {
        this(new PropertiesParser());
    }

    protected TrophyParser(PropertiesParser parser) {
        this.parser = parser;
    }

    public List<Trophy> parse(String content) {
        List trophies = new ArrayList();

        List<Properties> propertiesList = parser.parse(content);

        for (Properties properties : propertiesList) {
            int id = properties.getInt("id");
            if (id == 0) break;
            String title = properties.get("title");
            Trophy.Difficulty difficulty = Trophy.Difficulty.valueOf(properties.get("difficulty").toUpperCase());
            String description = properties.get("description");
            URL imageUrl = properties.getUrl("image_url");
            String achieved = properties.get("achieved");

            Trophy trophy = new Trophy();
            trophy.setId(id);
            trophy.setTitle(title);
            trophy.setDifficulty(difficulty);
            trophy.setDescription(description);
            trophy.setImageUrl(imageUrl);

            if ("false".equals(achieved)) {
                trophy.setAchieved(false);
                trophy.setTime("");
            } else {
                trophy.setAchieved(true);
                trophy.setTime(achieved);
            }

            trophies.add(trophy);
        }

        return trophies;
    }
}
