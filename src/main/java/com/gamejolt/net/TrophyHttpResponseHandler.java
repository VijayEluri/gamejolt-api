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

import com.gamejolt.Trophy;
import com.gamejolt.util.TrophyParser;

import java.util.List;

public abstract class TrophyHttpResponseHandler extends HttpResponseHandlerAdapter {
    private final TrophyParser trophyParser;


    public TrophyHttpResponseHandler(TrophyParser trophyParser) {
        this.trophyParser = trophyParser;
    }

    @Override
    public final void handle(HttpResponse response) {
        handle(trophyParser.parse(response.getContentAsString()));
    }

    protected abstract void handle(List<Trophy> trophies);
}
