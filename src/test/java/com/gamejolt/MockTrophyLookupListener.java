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

public class MockTrophyLookupListener implements TrophyLookupListener {
    private boolean found;
    private Trophy foundTrophy;

    @Override
    public void found(Trophy trophy) {
        found = true;
        foundTrophy = trophy;
    }

    @Override
    public void notFound(int trophyId) {
        found = false;
        foundTrophy = null;
    }

    public void assertFound(Trophy expectedTrophy) {
        assertFound();
        assertSame(expectedTrophy, foundTrophy);
    }

    public void assertFound() {
        assertTrue("we expected the trophy to be found", found);
    }

    public void assertNotFound() {
        assertFalse("we expected the trophy to NOT be found", found);
        assertNull(foundTrophy);
    }
}
