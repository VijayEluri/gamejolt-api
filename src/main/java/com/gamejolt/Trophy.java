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

package com.gamejolt;

import java.net.URL;


public class Trophy {
    public final int id;
    public final String title;
    public final Difficulty difficulty;
    public final String description;
    public final URL imageUrl;
    public final String timeOfAchievement;

    public Trophy(int id, String title, Difficulty difficulty, String description, URL imageUrl, String timeOfAchievement) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timeOfAchievement = timeOfAchievement;
    }

    public enum Difficulty {
        GOLD, SILVER, BRONZE
    }
}
