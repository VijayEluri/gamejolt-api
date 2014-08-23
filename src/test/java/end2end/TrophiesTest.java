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
import com.gamejolt.Trophy;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TrophiesTest extends End2EndTest {


    @Betamax(tape = "v1/unachieved trophies")
    @Test
    public void shouldAllowQueryingForUnachievedTrophies() {
        List<Trophy> trophies = gameJolt.getUnachievedTrophies();

        int i = 0;
        assertBronzeTrophy(trophies.get(i++));
        assertSilverTrophy(trophies.get(i++));
        assertGoldTrophy(trophies.get(i++));
        assertEquals(i, trophies.size());
    }

    @Betamax(tape = "v1/achieved trophies")
    @Test
    public void shouldAllowQueryingForAchievedTrophies() {
        List<Trophy> trophies = gameJolt.getAchievedTrophies();

        assertPlatinumTrophy(trophies.get(0));
        assertEquals(1, trophies.size());
    }

    @Betamax(tape = "v1/trophy achieved")
    @Test
    public void shouldAllowAchievingATrophyById() {
        assertTrue(gameJolt.achievedTrophy(10625));
    }
    
    @Betamax(tape = "v1/trophy by id is not found")
    @Test
    public void shouldReturnNullWhenATrophyIsNotFound() {
        assertNull(gameJolt.getTrophy(Integer.MAX_VALUE));
    }

    @Betamax(tape = "v1/trophy by id")
    @Test
    public void shouldAllowQueryingATrophyById() {
        assertPlatinumTrophy(gameJolt.getTrophy(10625));
    }

    @Betamax(tape = "v1/all available trophies")
    @Test
    public void shouldAllowSeeingAllAvailableTrophies() {
        List<Trophy> trophies = gameJolt.getAllTrophies();

        int i = 0;
        assertBronzeTrophy(trophies.get(i++));
        assertSilverTrophy(trophies.get(i++));
        assertGoldTrophy(trophies.get(i++));
        assertPlatinumTrophy(trophies.get(i++));
        assertEquals(i, trophies.size());
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
