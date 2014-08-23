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

import co.freeside.betamax.Recorder;
import com.gamejolt.GameJolt;
import org.junit.Before;
import org.junit.Rule;

public abstract class End2EndTest {
    @Rule public Recorder recorder = new Recorder();
    protected GameJolt gameJolt;

    @Before
    public void setUp() throws Exception {
        gameJolt = new GameJolt(2338, "6492144b383664fa32aa744e1c8f532d");
        gameJolt.verifyUser("born2snipe", "b7286a");
    }
}
