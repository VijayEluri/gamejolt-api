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

package com.gamejolt.net.simple;

import com.gamejolt.net.HttpRequest;
import com.gamejolt.net.HttpRequestException;
import com.gamejolt.net.HttpResponseHandler;
import com.gamejolt.net.QueryStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class SimpleHttpRequest implements HttpRequest {
    private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private String url;
    private boolean verbose = false;

    public SimpleHttpRequest(String url) {
        this(url, false);
    }

    public SimpleHttpRequest(String url, boolean verbose) {
        this.url = url;
        this.verbose = verbose;
    }

    public HttpRequest addParameter(String name, String value) {
        queryStringBuilder.parameter(name, value);
        return this;
    }

    public void addParameters(Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    public void execute(HttpResponseHandler handler) {
        SimpleHttpResponse response = performRequest();
        if (!response.isSuccessful()) {
            throw new HttpRequestException("Bad Http Response received response code " + response.getCode());
        }

        handler.handle(response);
    }

    private SimpleHttpResponse performRequest() {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            showRequest(verbose);
            URL request = new URL(buildUrlWithParameters());

            connection = (HttpURLConnection) request.openConnection();
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showResponseFailed(verbose, responseCode);
                return new SimpleHttpResponse(responseCode, new byte[0]);
            }

            input = connection.getInputStream();
            List<String> contentType = connection.getHeaderFields().get("Content-Encoding");
            if (isResponseCompressed(contentType, "gzip")) {
                input = new GZIPInputStream(input);
            }
            byte[] responseContent = readAll(input);
            showResponse(verbose, responseContent);
            return new SimpleHttpResponse(responseCode, responseContent);
        } catch (IOException e) {
            throw new HttpRequestException(e);
        } finally {
            close(connection);
            close(input);
        }
    }

    private String buildUrlWithParameters() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(url);
        String queryString = queryStringBuilder.toString();
        if (queryString.length() > 0) {
            builder.append(queryString);
        }
        return builder.toString();
    }

    private boolean isResponseCompressed(List<String> contentType, String algorithm) {
        if (contentType == null) {
            return false;
        }
        for (String type : contentType) {
            if (type.toLowerCase().contains(algorithm)) {
                return true;
            }
        }
        return false;
    }

    private void showResponseFailed(boolean verbose, int code) {
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("HTTP RESPONSE FAILED");
            System.out.println("-----------------------");
            System.out.println(String.valueOf(code));
            System.out.println("-----------------------");
            System.out.flush();
        }
    }

    private void showResponse(boolean verbose, byte[] responseContent) {
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("HTTP RESPONSE SUCCESS");
            System.out.println("-----------------------");
            System.out.println(new String(responseContent));
            System.out.println("-----------------------");
            System.out.flush();
        }
    }

    private void showRequest(boolean verbose) {
        if (verbose) {
            System.out.println("-----------------------");
            System.out.println("HTTP REQUEST");
            System.out.println("-----------------------");
            System.out.println(getUrl());
            System.out.println("-----------------------");
            System.out.flush();
        }
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
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (IOException e) {

        }
    }

    private void close(HttpURLConnection connection) {
        if (connection == null) {
            return;
        }
        connection.disconnect();
    }

    public String getUrl() {
        return url + queryStringBuilder.toString();
    }

    public String toString() {
        return getUrl();
    }

}
