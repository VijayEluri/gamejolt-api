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

import java.io.*;


public class StandardJavaObjectSerializer implements ObjectSerializer {
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(baos);
            output.writeObject(obj);
        } catch (IOException err) {
            throw new GameJoltException(err);
        }
        return baos.toByteArray();
    }

    public Object deserialize(byte[] data) {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new ByteArrayInputStream(data));
            return input.readObject();
        } catch (IOException err) {
            throw new GameJoltException(err);
        } catch (ClassNotFoundException err) {
            throw new GameJoltException(err);
        }
    }
}
