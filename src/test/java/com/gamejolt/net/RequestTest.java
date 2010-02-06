package com.gamejolt.net;

import com.gamejolt.GameJoltException;
import org.junit.Test;

import static org.junit.Assert.*;


public class RequestTest {
    @Test
    public void test_InValidUrl() {
        Request request = new Request("http://www.doesNotExistShouldThrowAHorribleError.com");

        try {
            request.doGet();
            fail();
        } catch (GameJoltException err) {

        }
    }

    @Test
    public void test_NoParameter() {
        Request request = new Request("http://www.google.com");

        Response response = request.doGet();

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertEquals(200, response.code);
    }

    @Test
    public void test_BadUrl() {
        Request request = new Request("http://www.google.com");
        request.addParameter("q", "java");

        Response response = request.doGet();

        assertEquals(0, response.getContent().length);
        assertEquals(404, response.code);
    }


}
