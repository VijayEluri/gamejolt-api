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

import static org.junit.Assert.*;

public class MockTrophyAchievedListener implements TrophyAchievedListener {
    private boolean achieved = false;
    private Trophy actualTrophy;

    public void achieved(Trophy trophy) {
        this.actualTrophy = trophy;
        achieved = true;
    }

    public void assertNotAchieved() {
        assertFalse("we expected the trophy to not be achieved", achieved);
    }

    public void assertAchieved(Trophy trophy) {
        assertSame(trophy, actualTrophy);
        assertAchieved();
    }

    public void assertAchieved() {
        assertTrue("we expected the trophy to be achieved", achieved);
    }
}
