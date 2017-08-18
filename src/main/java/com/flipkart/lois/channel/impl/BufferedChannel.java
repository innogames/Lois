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

package com.flipkart.lois.channel.impl;

import com.flipkart.lois.channel.api.Channel;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A Bufferred Channel that can contain "buffer size" messages before it
 * blocks on send.
 *
 * @param <T>
 */

public class BufferedChannel<T> implements Channel<T> {

    protected final ArrayBlockingQueue<T> buffer;

    protected volatile boolean isChannelOpen = true;

    public BufferedChannel(final int bufferSize) {
        this.buffer = new ArrayBlockingQueue<>(bufferSize, true);
    }

    @Override
    public T receive() {
        T message = null;
        while (isOpen() && message == null) {
            message = this.receive(Duration.ofMillis(2));
        }
        return message;
    }

    @Override
    public T receive(Duration timeOut) {
        T message = null;
        if (isOpen()) {
            try {
                message = buffer.poll(timeOut.toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //nothing to do
            }
        }
        return message;
    }

    @Override
    public boolean hasNext() {
        return isOpen() && buffer.size() > 0;
    }

    @Override
    public boolean send(final T message) {
        boolean sent = false;
        while (isOpen() && !sent) {
            sent = send(message, Duration.ofMillis(2));
        }
        return sent;
    }

    @Override
    public boolean send(final T message, Duration timeout) {
        boolean sent = false;
        if (isOpen()) {
            try {
                sent = buffer.offer(message, timeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                sent = false;
            }
        }
        return sent;
    }

    @Override
    public void close() {
        isChannelOpen = false;
    }

    @Override
    public boolean isOpen() {
        return isChannelOpen;
    }
}
