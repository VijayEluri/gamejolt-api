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
    private int id;
    private String title;
    private Difficulty difficulty;
    private String description;
    private URL imageUrl;
    private String time;
    private boolean achieved;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public String getTime() {
        return time;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public enum Difficulty {
        GOLD, SILVER, BRONZE, PLATINUM
    }
}
