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

import com.gamejolt.GameJolt;
import com.gamejolt.LoggingTrophyAchievedListener;
import com.gamejolt.Trophy;
import com.gamejolt.TrophyLookupListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages your trophy rules and your trophy achievement listeners
 */
public class TrophyManager {
    private final Map<Integer, TrophyAndRuleHolder> holders = new LinkedHashMap<Integer, TrophyAndRuleHolder>();
    private final GameJolt gameJolt;
    private List<TrophyManagerListener> listeners = new ArrayList<TrophyManagerListener>();
    private boolean batchListenerNotification;

    public TrophyManager(GameJolt gameJolt) {
        this.gameJolt = gameJolt;
    }

    /**
     * Register a rule to be used to determine if a trophy has been achieved or not
     *
     * @param trophyId   - the id of the trophy
     * @param trophyRule - the trophy rule class
     * @throws TrophyNotFoundException is thrown when the given trophy id could not be found
     */
    public void registerRule(final int trophyId, final AcquiredTrophyRule trophyRule) throws TrophyNotFoundException {
        gameJolt.getTrophy(trophyId, new TrophyLookupListener() {
            public void found(Trophy trophy) {
                holders.put(trophyId, new TrophyAndRuleHolder(trophy, trophyRule));
            }

            public void notFound(int trophyId) {
                throw new TrophyNotFoundException(trophyId);
            }
        });
    }

    /**
     * Executes all rules against the given context to determine if any trophies have been achieved
     *
     * @param context - the data to be passed to your trophy rules
     */
    public void manage(TrophyContext context) {
        List<Trophy> trophiesAcquired = new ArrayList<Trophy>();
        for (TrophyAndRuleHolder holder : holders.values()) {
            if (!holder.trophy.isAchieved() && holder.rule.acquired(context)) {
                gameJolt.achievedTrophy(holder.trophy.getId(), new LoggingTrophyAchievedListener());
                holder.trophy.setAchieved(true);
                if (batchListenerNotification) {
                    trophiesAcquired.add(holder.trophy);
                } else {
                    for (TrophyManagerListener listener : listeners) {
                        listener.trophiesAcquired(Arrays.asList(holder.trophy), context);
                    }
                }
            }
        }

        if (batchListenerNotification) {
            for (TrophyManagerListener listener : listeners) {
                listener.trophiesAcquired(trophiesAcquired, context);
            }
        }
    }

    /**
     * Register listener(s) to be notified of trophy achievements
     *
     * @param listener - the listener to be notified
     */
    public void addListener(TrophyManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Set this flag if you want your listener to be notified of all trophy achievements or an achievement at a time
     *
     * @param batchListenerNotification - <p>true - turn on batching</p><p>false - turn off batching</p>
     */
    public void setBatchListenerNotification(boolean batchListenerNotification) {
        this.batchListenerNotification = batchListenerNotification;
    }

    private static class TrophyAndRuleHolder {
        public final Trophy trophy;
        public final AcquiredTrophyRule rule;

        private TrophyAndRuleHolder(Trophy trophy, AcquiredTrophyRule rule) {
            this.trophy = trophy;
            this.rule = rule;
        }
    }
}