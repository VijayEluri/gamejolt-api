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

import com.gamejolt.GameJoltException;
import com.gamejolt.util.Base64;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class BinarySanitizer {
    private Base64 encoder = new Base64();

    public String sanitize(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream output = null;
        try {
            output = new GZIPOutputStream(baos);
            output.write(data);
            output.flush();
        } catch (IOException err) {
            throw new GameJoltException(err);
        } finally {
            try {
                if (output != null) output.close();
            } catch (IOException e) {

            }
        }
        return encoder.encode(baos.toByteArray());
    }

    public byte[] unsanitize(String data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(encoder.decode(data));

        InputStream input = null;
        try {
            input = new GZIPInputStream(bais);
            byte[] buffer = new byte[2048];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException err) {

        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException e) {

            }
        }
        return baos.toByteArray();
    }
}
