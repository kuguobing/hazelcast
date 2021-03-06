/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.topic;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.*;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author ali 5/24/13
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClientTopicTest {

    static final String name = "test1";
    static HazelcastInstance hz;
    static HazelcastInstance server;
    static ITopic t;

    @Before
    public void init(){
        server = Hazelcast.newHazelcastInstance();
        hz = HazelcastClient.newHazelcastClient(null);
        t = hz.getTopic(name);
    }

    @After
    public void stop(){
        hz.getLifecycleService().shutdown();
        Hazelcast.shutdownAll();
    }

    @Test
    public void testListener() throws Exception {

        final CountDownLatch latch = new CountDownLatch(10);
        MessageListener listener = new MessageListener() {
            public void onMessage(Message message) {
                latch.countDown();
            }
        };
        t.addMessageListener(listener);

        for (int i=0; i<10; i++){
            t.publish("naber"+i);
        }
        assertTrue(latch.await(20, TimeUnit.SECONDS));

    }
}
