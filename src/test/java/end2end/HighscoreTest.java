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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HighscoreTest extends End2EndTest {
    @Betamax(tape = "v1/top 10 user high scores")
    @Test
    public void shouldAllowQueryingForTheTop10UserScores() {
        whenUserHasThisManyScores(11);
        assertEquals(10, gameJolt.getTop10UserHighscores().size());
    }

    @Betamax(tape = "v1/limited number of user high scores")
    @Test
    public void shouldAllowQueryingTheUserHighScores() {
        whenUserHasThisManyScores(101);
        gameJolt.getUserHighscores(100);
    }

    @Betamax(tape = "v1/top 10 of all high scores")
    @Test
    public void shoudlAllowQueryingForTheTopTenScores() {
        whenUserHasThisManyScores(11);
        assertEquals(10, gameJolt.getTop10Highscores().size());
    }

    @Betamax(tape = "v1/limited number of all high scores")
    @Test
    public void shouldAllowRetrievingAllHighScores() {
        whenUserHasThisManyScores(101);

        assertEquals(100, gameJolt.getAllHighscores(100).size());
    }

    @Betamax(tape = "v1/achieved high score")
    @Test
    public void shouldAllowStoringAHighScore() {
        assertTrue(gameJolt.userAchievedHighscore(100));
    }

    private void whenUserHasThisManyScores(int scoreCount) {
        for (int i = 0; i < scoreCount; i++) {
            gameJolt.userAchievedHighscore(i);
        }
    }

}
