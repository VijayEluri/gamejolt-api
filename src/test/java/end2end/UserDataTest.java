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
import com.gamejolt.MockDataKeysListener;
import com.gamejolt.MockListener;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserDataTest extends End2EndTest {
    private MockListener listener;
    private MockDataKeysListener dataKeysListener;

    @Before
    public void setUp() throws Exception {
        listener = new MockListener();
        dataKeysListener = new MockDataKeysListener();
    }

    @Betamax(tape = "v1/delete all user data")
    @Test
    public void shouldAllowDeletingAllTheData() {
        storeUserData("data-1", 1);
        storeUserData("data-2", 2);

        gameJolt.clearAllUserData(listener);

        listener.assertSuccess();
    }

    @Betamax(tape = "v1/all user data keys")
    @Test
    public void shouldAllowQueryingForAllDataKeys() {
        storeUserData("data-1", 1);
        storeUserData("data-2", 2);

        gameJolt.getUserDataKeys(dataKeysListener);

        dataKeysListener.assertKeys("data-1", "data-2");
    }

    @Betamax(tape = "v1/all user data")
    @Test
    public void shouldAllowQueryingForAllTheUserData() {
        storeUserData("data-1", 1);
        storeUserData("data-2", 2);

        Map<String, Object> data = gameJolt.loadAllUserData();

        assertEquals(1, data.get("data-1"));
        assertEquals(2, data.get("data-2"));
    }

    @Betamax(tape = "v1/delete user data")
    @Test
    public void shouldAllowDeletingDataForTheUser() {
        storeUserData("data-key", "data-value");
        gameJolt.removeUserData("data-key", listener);
        listener.assertSuccess();
    }

    @Betamax(tape = "v1/retrieve user data that does not exist")
    @Test
    public void shouldReturnNullWhenThereIsNoDataFound() {
        assertNull(gameJolt.getUserData("doesNotExist"));
    }

    @Betamax(tape = "v1/retrieve user data")
    @Test
    public void shouldAllowRetrievingDataForTheUser() {
        storeUserData("data-key", "data-value");
        assertEquals("data-value", gameJolt.getUserData("data-key"));
    }

    @Betamax(tape = "v1/storing user data")
    @Test
    public void shouldAllowStoringDataForTheUser() {
        gameJolt.storeUserData("data-key", "data-value", listener);
        listener.assertSuccess();
    }

    private void storeUserData(String name, Object data) {
        gameJolt.storeUserData(name, data, new MockListener());
    }
}
