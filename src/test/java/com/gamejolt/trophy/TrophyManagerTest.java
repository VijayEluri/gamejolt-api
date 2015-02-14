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


import com.gamejolt.MockGameJolt;
import com.gamejolt.Trophy;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class TrophyManagerTest {
    private MockGameJolt gameJolt;
    private TrophyManager manager;
    private AcquiredTrophyRule rule;
    private TrophyContext context;
    private TrophyManagerListener listener;

    @Before
    public void setUp() throws Exception {
        gameJolt = new MockGameJolt();
        rule = mock(AcquiredTrophyRule.class);
        listener = mock(TrophyManagerListener.class);

        manager = new TrophyManager(gameJolt);
        manager.addListener(listener);

        context = new TrophyContext();
    }

    @Test
    public void test_manage_singleRule_MultipleListeners() {
        TrophyManagerListener listener2 = mock(TrophyManagerListener.class);
        manager.addListener(listener2);

        Trophy trophy = new Trophy();
        gameJolt.addTrophy(123, trophy);
        when(rule.acquired(context)).thenReturn(true);

        manager.registerRule(123, rule);
        manager.manage(context);

        verify(listener).trophiesAcquired(asList(trophy), context);
        verify(listener2).trophiesAcquired(asList(trophy), context);
    }

    @Test
    public void test_manage_singleRule_TrophyNotAcquired() {
        Trophy trophy = new Trophy();
        gameJolt.addTrophy(123, trophy);
        when(rule.acquired(context)).thenReturn(false);

        manager.registerRule(123, rule);
        manager.manage(context);

        verifyZeroInteractions(listener);
    }

    @Test
    public void test_manage_singleRule_TrophyAlreadyAcquired() {
        Trophy trophy = new Trophy();
        trophy.setAchieved(true);
        gameJolt.addTrophy(123, trophy);

        manager.registerRule(123, rule);
        manager.manage(context);

        verifyZeroInteractions(rule, listener);
    }

    @Test
    public void test_manage_singleRule_TrophyAcquired() {
        Trophy trophy = new Trophy();
        trophy.setId(123);
        gameJolt.addTrophy(123, trophy);
        gameJolt.whenTrophyIsAchieved(123);
        when(rule.acquired(context)).thenReturn(true);

        manager.registerRule(123, rule);
        manager.manage(context);

        verify(rule).acquired(context);
        verify(listener).trophiesAcquired(asList(trophy), context);
    }

    @Test
    public void test_manage_mutipleRules_AllTrophiesAcquired() {
        Trophy trophy = new Trophy();
        Trophy trophy2 = new Trophy();
        AcquiredTrophyRule rule2 = mock(AcquiredTrophyRule.class);

        gameJolt.addTrophy(123, trophy);
        gameJolt.addTrophy(456, trophy2);

        when(rule.acquired(context)).thenReturn(true);
        when(rule2.acquired(context)).thenReturn(true);

        manager.registerRule(123, rule);
        manager.registerRule(456, rule2);
        manager.manage(context);

        verify(rule).acquired(context);
        verify(listener).trophiesAcquired(asList(trophy), context);
        verify(listener).trophiesAcquired(asList(trophy2), context);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void test_manage_mutipleRules_AllTrophiesAcquired_BatchNotification() {
        manager.setBatchListenerNotification(true);

        Trophy trophy = new Trophy();
        Trophy trophy2 = new Trophy();
        AcquiredTrophyRule rule2 = mock(AcquiredTrophyRule.class);

        gameJolt.addTrophy(123, trophy);
        gameJolt.addTrophy(456, trophy2);

        when(rule.acquired(context)).thenReturn(true);
        when(rule2.acquired(context)).thenReturn(true);

        manager.registerRule(123, rule);
        manager.registerRule(456, rule2);
        manager.manage(context);

        verify(rule).acquired(context);
        verify(listener).trophiesAcquired(asList(trophy, trophy2), context);
        verifyNoMoreInteractions(listener);

        assertTrue(trophy.isAchieved());
        assertTrue(trophy2.isAchieved());
    }

    @Test
    public void test_manage_mutipleRules_AllTrophiesAcquired_BatchNotification_MultipleListeners() {
        manager.setBatchListenerNotification(true);

        TrophyManagerListener listener2 = mock(TrophyManagerListener.class);
        manager.addListener(listener2);

        Trophy trophy = new Trophy();
        Trophy trophy2 = new Trophy();
        AcquiredTrophyRule rule2 = mock(AcquiredTrophyRule.class);

        gameJolt.addTrophy(123, trophy);
        gameJolt.addTrophy(456, trophy2);

        when(rule.acquired(context)).thenReturn(true);
        when(rule2.acquired(context)).thenReturn(true);

        manager.registerRule(123, rule);
        manager.registerRule(456, rule2);
        manager.manage(context);

        verify(rule).acquired(context);
        verify(listener).trophiesAcquired(asList(trophy, trophy2), context);
        verify(listener2).trophiesAcquired(asList(trophy, trophy2), context);
        verifyNoMoreInteractions(listener, listener2);
    }


    @Test
    public void test_registerRule_CouldNotFindTrophy() {
        try {
            manager.registerRule(123, rule);
            fail();
        } catch (TrophyNotFoundException err) {
            assertEquals("Could not locate trophy with id=123", err.getMessage());
        }
    }

}
