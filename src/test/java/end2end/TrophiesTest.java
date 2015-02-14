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
package end2end;

import co.freeside.betamax.Betamax;
import com.gamejolt.MockTrophyAchievedListener;
import com.gamejolt.TrophiesLookupListener;
import com.gamejolt.Trophy;
import com.gamejolt.TrophyLookupListener;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TrophiesTest extends End2EndTest {


    @Betamax(tape = "v1/unachieved trophies")
    @Test
    public void shouldAllowQueryingForUnachievedTrophies() {
        gameJolt.getUnachievedTrophies(new TrophiesLookupListener() {
            public void foundTrophies(List<Trophy> trophies) {
                int i = 0;
                assertBronzeTrophy(trophies.get(i++));
                assertSilverTrophy(trophies.get(i++));
                assertGoldTrophy(trophies.get(i++));
                assertEquals(i, trophies.size());
            }
        });
    }

    @Betamax(tape = "v1/achieved trophies")
    @Test
    public void shouldAllowQueryingForAchievedTrophies() {
        gameJolt.getAchievedTrophies(new TrophiesLookupListener() {
            public void foundTrophies(List<Trophy> trophies) {
                assertPlatinumTrophy(trophies.get(0));
                assertEquals(1, trophies.size());
            }
        });
    }

    @Betamax(tape = "v1/trophy achieved")
    @Test
    public void shouldAllowAchievingATrophyById() {
        MockTrophyAchievedListener listener = new MockTrophyAchievedListener();
        gameJolt.achievedTrophy(10625, listener);
        listener.assertAchieved();
    }

    @Betamax(tape = "v1/trophy by id is not found")
    @Test
    public void shouldReturnNullWhenATrophyIsNotFound() {
        final int id = Integer.MAX_VALUE;
        gameJolt.getTrophy(id, new TrophyLookupListener() {
            @Override
            public void found(Trophy trophy) {
                fail("We should have NOT found the trophy");
            }

            @Override
            public void notFound(int trophyId) {
                assertEquals(id, trophyId);
            }
        });
    }

    @Betamax(tape = "v1/trophy by id")
    @Test
    public void shouldAllowQueryingATrophyById() {
        gameJolt.getTrophy(10625, new TrophyLookupListener() {
            public void found(Trophy trophy) {
                assertPlatinumTrophy(trophy);
            }

            public void notFound(int trophyId) {
                fail("We should have found the trophy");
            }
        });
    }

    @Betamax(tape = "v1/all available trophies")
    @Test
    public void shouldAllowSeeingAllAvailableTrophies() {
        gameJolt.getAllTrophies(new TrophiesLookupListener() {
            public void foundTrophies(List<Trophy> trophies) {
                int i = 0;
                assertBronzeTrophy(trophies.get(i++));
                assertSilverTrophy(trophies.get(i++));
                assertGoldTrophy(trophies.get(i++));
                assertPlatinumTrophy(trophies.get(i++));
                assertEquals(i, trophies.size());
            }
        });
    }

    private void assertGoldTrophy(Trophy trophy) {
        assertTrophy(Trophy.Difficulty.GOLD, "Gold Trophy", trophy);
    }

    private void assertSilverTrophy(Trophy trophy) {
        assertTrophy(Trophy.Difficulty.SILVER, "Silver Trophy", trophy);
    }

    private void assertBronzeTrophy(Trophy trophy) {
        assertTrophy(Trophy.Difficulty.BRONZE, "Bronze Trophy", trophy);
    }

    private void assertPlatinumTrophy(Trophy trophy) {
        assertTrophy(Trophy.Difficulty.PLATINUM, "Platinum Trophy", trophy);
    }

    private void assertTrophy(Trophy.Difficulty expectedDifficulty, String expectedTitle, Trophy trophy) {
        assertEquals(expectedDifficulty, trophy.getDifficulty());
        assertEquals(expectedTitle, trophy.getTitle());
        assertEquals("Test Trophy", trophy.getDescription());
    }

}
