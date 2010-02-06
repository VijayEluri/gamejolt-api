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

package com.gamejolt.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ChecksumTest {
    @Test
    public void test() {
        Checksum checksum = new Checksum();

        assertEquals("9e107d9d372bb6826bd81d3542a419d6", checksum.md5("The quick brown fox jumps over the lazy dog"));
        assertEquals("e4d909c290d0fb1ca068ffaddf22cbd0", checksum.md5("The quick brown fox jumps over the lazy dog."));
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", checksum.md5(""));
    }
}
