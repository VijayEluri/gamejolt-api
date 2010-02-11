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

import java.util.ArrayList;
import java.util.List;


public class HighscoreParser {
    private PropertiesParser propertiesParser;

    public HighscoreParser() {
        this(new PropertiesParser());
    }

    protected HighscoreParser(PropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    public List<Highscore> parse(String content) {
        List<Highscore> scores = new ArrayList<Highscore>();

        List<Properties> propertiesList = propertiesParser.parse(content);

        for (Properties properties : propertiesList) {
            Highscore score = new Highscore();
            score.setDisplayedScore(properties.get("score"));
            score.setScore(properties.getInt("sort"));
            score.setExtraData(properties.get("extra_data"));
            score.setGuestScore(properties.isBlank("user"));
            if (properties.isBlank("user")) {
                score.setUser(properties.get("guest"));
            } else {
                score.setUser(properties.get("user"));
            }
            score.setUserId(properties.getInt("user_id"));
            score.setTimeOfScore(properties.get("stored"));

            scores.add(score);
        }

        return scores;
    }
}
