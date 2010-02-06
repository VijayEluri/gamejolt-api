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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class HttpRequest {
    private static final UrlFactory urlFactory = new UrlFactory();
    private static final int SUCCESS_RESPONSE_CODE = 200;
    private String url;
    private Map<String, String> parameters = new LinkedHashMap<String, String>();

    public HttpRequest(String url) {
        this.url = url;
    }


    public HttpRequest addParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public HttpRequest addParameter(String name, int value) {
        return addParameter(name, String.valueOf(value));
    }

    public HttpResponse doGet() throws GameJoltException {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL request = urlFactory.build(buildUrlWithParameters());
            connection = (HttpURLConnection) request.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != SUCCESS_RESPONSE_CODE) {
                return new HttpResponse(responseCode, new byte[0]);
            }
            input = connection.getInputStream();
            return new HttpResponse(responseCode, readAll(input));
        } catch (IOException e) {
            throw new GameJoltException(e);
        } finally {
            close(connection);
            close(input);
        }
    }

    private String buildUrlWithParameters() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(url);
        if (parameters.size() > 0) {
            builder.append("?");
            List<String> params = new ArrayList<String>();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                params.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            builder.append(join(params));
        }
        return builder.toString();
    }

    private String join(List<String> items) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            builder.append(items.get(i));
            if (i < items.size() - 1) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    private byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int length;
        while ((length = input.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }
        return output.toByteArray();
    }

    private void close(InputStream input) {
        if (input == null) return;
        try {
            input.close();
        } catch (IOException e) {

        }
    }

    private void close(HttpURLConnection connection) {
        if (connection == null) return;
        connection.disconnect();
    }

    public String getUrl() {
        try {
            return buildUrlWithParameters();
        } catch (UnsupportedEncodingException e) {
            throw new GameJoltException(e);
        }
    }

    public String toString() {
        return getUrl();
    }
}