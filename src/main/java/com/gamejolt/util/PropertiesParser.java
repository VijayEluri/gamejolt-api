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

import java.util.ArrayList;
import java.util.List;


public class PropertiesParser {
    private static final String MULTIPLE_INSTANCES = "Not a single instance of properties found ({0} instances)";

    public List<Properties> parse(String content) {
        List<Properties> properties = new ArrayList<Properties>();
        Properties current = new Properties();

        String[] lines = content.split("\r\n|\n");
        for (String line : lines) {
            int indexOfColon = line.indexOf(':');
            if (indexOfColon > -1) {
                String key = line.substring(0, indexOfColon);
                String value = line.substring(indexOfColon + 1).replace("\"", "").trim();
                if (current.contains(key)) {
                    properties.add(current);
                    current = new Properties();
                }
                current.put(key, value);
            }
        }

        properties.add(current);
        return properties;
    }

    public Properties parseProperties(String content) throws GameJoltException {
        List<Properties> list = parse(content);
        if (list.size() > 1) {
            throw new GameJoltException(MessageFormat.format(MULTIPLE_INSTANCES, list.size()));
        }
        return list.get(0);
    }

    public List<String> parseToList(String content, String key) {
        List<String> values = new ArrayList<String>();
        List<Properties> propertiesList = parse(content);
        for (Properties properties : propertiesList) {
            String value = properties.get(key);
            if (value != null) values.add(value);
        }
        return values;
    }
}
