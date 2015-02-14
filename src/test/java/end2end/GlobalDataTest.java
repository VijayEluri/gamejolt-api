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
import com.gamejolt.MockListener;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GlobalDataTest extends End2EndTest {
    private MockListener listener;

    @Before
    public void setUp() throws Exception {
        listener = new MockListener();
    }

    @Betamax(tape = "v1/get all global data")
    @Test
    public void shouldAllowRetrievingAllGlobalData() {
        storeGameData("data-1", 1);
        storeGameData("data-2", 2);

        Map<String, Object> data = gameJolt.loadAllGameData();

        assertEquals(1, data.get("data-1"));
        assertEquals(2, data.get("data-2"));
    }

    @Betamax(tape = "v1/delete global data")
    @Test
    public void shouldAllowRemoveDataByKey() {
        storeGameData("data-1", "1");

        gameJolt.removeGameData("data-1", listener);

        listener.assertSuccess();
    }

    @Betamax(tape = "v1/retrieve global data")
    @Test
    public void shouldAllowRetrievingGlobalData() {
        storeGameData("data-1", "1");

        assertEquals("1", gameJolt.getGameData("data-1"));
    }

    @Betamax(tape = "v1/store global data")
    @Test
    public void shouldAllowStoringGlobalData() {
        gameJolt.storeGameData("data-1", "1", listener);
        listener.assertSuccess();
    }

    @Betamax(tape = "v1/clear all global data")
    @Test
    public void shouldAllowClearingAllData() {
        storeGameData("data-1", "1");
        storeGameData("data-2", "2");

        gameJolt.clearAllGameData(listener);

        listener.assertSuccess();
    }

    private void storeGameData(String name, Object data) {
        gameJolt.storeGameData(name, data, new MockListener());
    }
}
