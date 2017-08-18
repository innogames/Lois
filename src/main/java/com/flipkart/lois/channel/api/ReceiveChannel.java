/**
 * Copyright 2014 Flipkart Internet, pvt ltd.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.lois.channel.api;

import java.time.Duration;

/**
 * A simple "go" like channel abstraction for java
 * over which messages can only be received
 *
 * @param <T>
 */
public interface ReceiveChannel<T> {

    /**
     * Tries to receive a message in a blocking way. If the channel get closed "null" is returned
     * else the value that is received is returned.
     *
     * @return a message of Type {@link T}
     */
    //maybe optionals
    T receive();

    /**
     * Receive a message of Type {@link T} if message is available or block the thread and put it in wait state
     * until there is a message available or timeout {@link java.util.concurrent.TimeUnit}'s have passed. If the timeout has
     * been breached null value will returned.
     *
     * @param timeout the maximum amount of time to wait
     * @return a message of Type {@link T}
     */
    T receive(Duration timeout);

    boolean hasNext();
}
