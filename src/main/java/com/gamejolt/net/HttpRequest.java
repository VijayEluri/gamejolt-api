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
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;


public class HttpRequest {
    private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private static final HttpClient http = new HttpClient();
    private HttpMethod requestMethod;
    private String url;

    public HttpRequest(String url) {
        this(url, false);
    }

    public HttpRequest(String url, boolean isPost) {
        if (isPost) {
            requestMethod = new PostMethod(url);
        } else {
            requestMethod = new GetMethod(url);
        }
        this.url = url;
    }


    public HttpRequest addParameter(String name, String value) {
        queryStringBuilder.parameter(name, value);
        return this;
    }

    public HttpRequest addParameter(String name, int value) {
        return addParameter(name, String.valueOf(value));
    }

    public void addParameters(Map<String, String> parameters) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    public HttpResponse doGet(boolean verbose) throws GameJoltException {
        InputStream input = null;
        try {
            requestMethod.setQueryString(queryStringBuilder.toString());
            showRequest(verbose);
            requestMethod.setRequestHeader("Accept-Encoding", "gzip,deflate");
            requestMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
            int responseCode = http.executeMethod(requestMethod);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                showResponseFailed(verbose, responseCode);
                return new HttpResponse(responseCode, new byte[0]);
            }

            input = requestMethod.getResponseBodyAsStream();
            Header header = requestMethod.getResponseHeader("Content-Encoding");
            if (isResponseCompressed(header, "gzip")) {
                input = new GZIPInputStream(input);
            } else if (isResponseCompressed(header, "deflate")) {
                input = new DeflaterInputStream(input);
            }
            byte[] responseContent = readAll(input);
            showResponse(verbose, responseContent);
            return new HttpResponse(responseCode, responseContent);
        } catch (IOException e) {
            throw new GameJoltException(e);
        } finally {
            requestMethod.releaseConnection();
            close(input);
        }
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

    private boolean isResponseCompressed(Header header, String algorithm) {
        if (header == null) return false;
        return algorithm.equalsIgnoreCase(header.getValue());
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

    public String getUrl() {
        return url + queryStringBuilder.toString();
    }

    public String toString() {
        return getUrl();
    }

    public boolean isPost() {
        return requestMethod instanceof PostMethod;
    }

}
