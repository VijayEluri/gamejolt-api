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
package com.gamejolt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MockUserVerificationListener implements UserVerificationListener {
    private boolean verified;

    public void failedVerification(String username) {
        this.verified = false;
    }

    public void verified(String username) {
        this.verified = true;
    }

    public void assertNotVerified() {
        assertFalse("we expected the user to NOT be verified", verified);
    }

    public void assertVerified() {
        assertTrue("we expected the user to be verified", verified);
    }
}
