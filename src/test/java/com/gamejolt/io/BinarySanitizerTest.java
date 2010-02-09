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

package com.gamejolt.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BinarySanitizerTest {
    private static final String ENCODED = "c3RyaW5nLWRhdGE=";
    private static final String DECODED = "string-data";

    @Test
    public void test() {
        BinarySanitizer sanitizer = new BinarySanitizer();

        assertEquals(ENCODED, sanitizer.sanitize(DECODED.getBytes()));
        assertEquals(DECODED, new String(sanitizer.unsanitize(ENCODED)));
    }

}
