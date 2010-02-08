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

package com.gamejolt.net;

import com.gamejolt.util.Checksum;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SignatureFactoryTest {
    private Checksum checksum;
    private SignatureFactory factory;

    @Before
    public void setUp() throws Exception {
        checksum = mock(Checksum.class);

        factory = new SignatureFactory("private-key");
        factory.setChecksum(checksum);
    }

    @Test
    public void test() {
        when(checksum.md5("http://gamejolt.com/api/game/v1/users/auth/?game_id=1111&username=player&user_token=player-tokenprivate-key")).thenReturn("signature-hash");

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("game_id", "1111");
        parameters.put("username", "player");

        assertEquals("signature-hash", factory.build("http://gamejolt.com/api/game/v1/users/auth/", parameters, "player-token"));
    }

}
