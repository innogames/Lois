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


import com.flipkart.lois.Lois;
import com.flipkart.lois.channel.api.SendChannel;
import org.awaitility.Duration;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class BufferedSendChannelTest {

    @Test
    public void channel_open_after_creation() {
        //given
        //when
        SendChannel<String> channel = new BufferedChannel<>(1);

        //then
        assertThat(channel.isOpen()).isTrue();
    }

    @Test
    public void close_channel() {
        //given
        SendChannel<String> channel = new BufferedChannel<>(1);

        //when
        channel.close();

        //then
        assertThat(channel.isOpen()).isFalse();
    }

    @Test
    public void open_and_send_to_channel() {
        //given
        SendChannel<String> channel = new BufferedChannel<>(1);

        //when
        boolean success = channel.send("Hello");

        //then
        assertThat(channel.isOpen()).isTrue();
        assertThat(success).isTrue();
    }

    @Test
    public void closed_channel_return_false_after_send() {
        //given
        SendChannel<String> channel = new BufferedChannel<>(1);
        channel.close();
        //when
        boolean success = channel.send("Hello");

        //then
        assertThat(success).isFalse();
    }

    @Test
    public void sending_ten_times_should_block() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(10);
        SendChannel<String> channel = new BufferedChannel<>(10);

        //when
        Lois.go(() -> {
            while (true) {
                channel.send("Hello");
                latch.countDown();
            }
        });

        Thread.sleep(100);

        //then
        assertThat(channel.isOpen()).isTrue();
        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void call_to_close_should_unblock() throws InterruptedException {
        //given
        AtomicBoolean stopped = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(10);
        SendChannel<String> channel = new BufferedChannel<>(10);

        //when
        Lois.go(() -> {
            while (channel.send("Hello")) {
                latch.countDown();
            }
            stopped.set(true);
        });

        Thread.sleep(10);
        channel.close();

        //then
        await().timeout(Duration.ONE_SECOND).untilAsserted(() -> assertThat(stopped.get()).isTrue());
        assertThat(channel.isOpen()).isFalse();
        assertThat(latch.getCount()).isEqualTo(0);
    }
}
