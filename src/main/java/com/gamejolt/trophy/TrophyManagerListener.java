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

package com.gamejolt.trophy;

import com.gamejolt.Trophy;

import java.util.List;

/**
 * The listener class that gets notified when a trophy has been achieved
 */
public interface TrophyManagerListener {
    /**
     * One of your trophy rules have been triggered and now you are being notified
     *
     * @param trophies - the trophies that have been achieved
     * @param context  - the context data that was used to determine achievement
     */
    void trophiesAcquired(List<Trophy> trophies, TrophyContext context);
}
