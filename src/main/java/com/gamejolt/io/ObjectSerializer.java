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

/**
 * This class is used for serializing an object to and from bytes
 */
public interface ObjectSerializer {
    /**
     * Serializes the given object to a byte array
     *
     * @param obj - object to be serialized
     * @return the bytes that make up the given object
     */
    byte[] serialize(Object obj);

    /**
     * Deserializes the given byte array back to an Object
     *
     * @param data - the bytes to be deserialized
     * @return the object that makes up the given bytes
     */
    Object deserialize(byte[] data);
}
