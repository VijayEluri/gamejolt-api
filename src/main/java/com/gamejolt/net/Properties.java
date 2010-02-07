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

import com.gamejolt.GameJoltException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;


public class Properties {
    private Map<String, String> values = new LinkedHashMap<String, String>();

    public Properties(String value) {
        String[] lines = value.split("\r\n|\n");
        for (String line : lines) {
            String[] pieces = line.replaceAll("(.+?):(.+)", "$1###$2").split("###");
            values.put(pieces[0], pieces[1].replace("\"", ""));
        }
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public String get(String key) {
        return values.get(key);
    }

    public Map<String, String> asMap() {
        return values;
    }

    public URL getUrl(String key) {
        try {
            return new URL(get(key));
        } catch (MalformedURLException e) {
            throw new GameJoltException(e);
        }
    }
}
