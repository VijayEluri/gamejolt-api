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

import com.gamejolt.GameJoltException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Checksum {
    public String md5(String value) {
        return md5(value.getBytes());
    }

    public String md5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');

                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GameJoltException(e);
        }
    }
}
