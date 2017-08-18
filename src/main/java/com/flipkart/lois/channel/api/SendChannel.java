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
 * over which messages can only be sent
 *
 * @param <T>
 */
public interface SendChannel<T> {

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free put the calling thread in a wait state until it becomes available to send
     * a message.
     *
     * @param message
     */
    boolean send(T message);

    /**
     * Send a message of Type {@link T} if the channel hasn't been closed and is available to send a message.
     * If the channel is not free put the calling thread in a wait state until it becomes available to send
     * a message until timeOut {@link java.util.concurrent.TimeUnit}'s have passed, at which point throw a {@link java.util.concurrent.TimeoutException}.
     *
     * @param message
     * @param timeout
     */
    boolean send(T message, Duration timeout);

    /**
     * Close channel so that no new messages can be sent over this channel. The messages that are already available
     * on the channel can be consumed.
     */
    void close();

    /**
     * Returns true if channel is open and false if channel is closed.
     *
     * @return true if channel is open, false if channel is closed
     */
    boolean isOpen();
}
