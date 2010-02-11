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


public class Highscore {
    private String displayedScore;
    private int score;
    private String extraData;
    private String user;
    private int userId;
    private String timeOfScore;
    private boolean guestScore;

    public String getDisplayedScore() {
        return displayedScore;
    }

    public void setDisplayedScore(String displayedScore) {
        this.displayedScore = displayedScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isGuestScore() {
        return guestScore;
    }

    public void setGuestScore(boolean guestScore) {
        this.guestScore = guestScore;
    }

    public String getTimeOfScore() {
        return timeOfScore;
    }

    public void setTimeOfScore(String timeOfScore) {
        this.timeOfScore = timeOfScore;
    }
}
