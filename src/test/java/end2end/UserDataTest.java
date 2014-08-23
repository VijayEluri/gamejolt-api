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

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class UserDataTest extends End2EndTest {
    @Betamax(tape = "v1/delete all user data")
    @Test
    public void shouldAllowDeletingAllTheData() {
        gameJolt.storeUserData("data-1", 1);
        gameJolt.storeUserData("data-2", 2);

        gameJolt.clearAllUserData();
    }

    @Betamax(tape = "v1/all user data keys")
    @Test
    public void shouldAllowQueryingForAllDataKeys() {
        gameJolt.storeUserData("data-1", 1);
        gameJolt.storeUserData("data-2", 2);

        assertEquals(Arrays.asList("data-1", "data-2"),gameJolt.getUserDataKeys());
    }

    @Betamax(tape = "v1/all user data")
    @Test
    public void shouldAllowQueryingForAllTheUserData() {
        gameJolt.storeUserData("data-1", 1);
        gameJolt.storeUserData("data-2", 2);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertEquals(1, data.get("data-1"));
        assertEquals(2, data.get("data-2"));
    }

    @Betamax(tape = "v1/delete user data")
    @Test
    public void shouldAllowDeletingDataForTheUser() {
        gameJolt.storeUserData("data-key", "data-value");
        assertTrue(gameJolt.removeUserData("data-key"));
    }

    @Betamax(tape = "v1/retrieve user data that does not exist")
    @Test
    public void shouldReturnNullWhenThereIsNoDataFound() {
        assertNull(gameJolt.getUserData("doesNotExist"));
    }

    @Betamax(tape = "v1/retrieve user data")
    @Test
    public void shouldAllowRetrievingDataForTheUser() {
        gameJolt.storeUserData("data-key", "data-value");
        assertEquals("data-value", gameJolt.getUserData("data-key"));
    }

    @Betamax(tape = "v1/storing user data")
    @Test
    public void shouldAllowStoringDataForTheUser() {
        assertTrue(gameJolt.storeUserData("data-key", "data-value"));
    }
}
