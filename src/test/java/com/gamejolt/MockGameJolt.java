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

import java.util.HashMap;
import java.util.HashSet;

public class MockGameJolt extends GameJolt {
    private HashMap<Integer, Trophy> idToTrophy = new HashMap<Integer, Trophy>();
    private HashSet<Integer> achievedTrophies = new HashSet<Integer>();

    public MockGameJolt() {
        super(-1, "mock-game-jolt");
    }

    public void addTrophy(Integer id, Trophy trophy) {
        idToTrophy.put(id, trophy);
    }

    public void whenTrophyIsAchieved(int trophyId) {
        achievedTrophies.add(trophyId);
    }

    @Override
    public void achievedTrophy(int trophyId, TrophyAchievedListener listener) throws UnverifiedUserException {
        if (achievedTrophies.contains(trophyId)) {
            listener.achieved(idToTrophy.get(trophyId));
        }
    }

    @Override
    public void getTrophy(int trophyId, TrophyLookupListener listener) throws UnverifiedUserException {
        if (idToTrophy.containsKey(trophyId)) {
            listener.found(idToTrophy.get(trophyId));
        } else {
            listener.notFound(trophyId);
        }
    }

    @Override
    protected void assertVerified() {

    }
}
