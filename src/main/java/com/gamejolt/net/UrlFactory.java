package com.gamejolt.net;

import com.gamejolt.GameJoltException;

import java.net.MalformedURLException;
import java.net.URL;


class UrlFactory {
    public URL build(String url) throws GameJoltException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new GameJoltException(e);
        }
    }
}
