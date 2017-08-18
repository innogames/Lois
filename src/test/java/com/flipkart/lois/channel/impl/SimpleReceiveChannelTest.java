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
import com.flipkart.lois.channel.api.Channel;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleReceiveChannelTest {

    @Test
    public void open_and_receive_from_channel() {
        //given
        Channel<String> channel = new SimpleChannel<>();

        //when
        boolean success = channel.send("Hello");
        assertThat(channel.hasNext()).isTrue();
        String message = channel.receive();

        //then
        assertThat(channel.hasNext()).isFalse();
        assertThat(channel.isOpen()).isTrue();
        assertThat(success).isTrue();
        assertThat(message).isEqualTo("Hello");
    }

    @Test
    public void should_return_null_if_channel_is_closed() {
        //given
        Channel<String> channel = new SimpleChannel<>();
        channel.close();

        //when
        String receive = channel.receive();

        //then
        assertThat(receive).isNull();
    }

    @Test
    public void empty_channel_return_false_on_hasNext() {
        //given
        Channel<String> channel = new SimpleChannel<>();

        //when
        //then
        assertThat(channel.hasNext()).isFalse();

    }

    @Test
    public void close_channel_return_false_on_hasNext() {
        //given
        Channel<String> channel = new SimpleChannel<>();
        channel.close();
        //when
        //then
        assertThat(channel.hasNext()).isFalse();
    }

    @Test
    public void channel_return_true_on_hasNext_if_is_open_and_filled() {
        //given
        Channel<String> channel = new SimpleChannel<>();

        //when
        channel.send("Hello");

        //then
        assertThat(channel.hasNext()).isTrue();
    }

    @Test
    public void receiving_second_time_should_block() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(5);
        Channel<String> channel = new SimpleChannel<>();

        channel.send("Hello");
        //when
        Lois.go(() -> {
            while (channel.receive() != null) {
                latch.countDown();
            }
        });

        Thread.sleep(100);

        //then
        assertThat(channel.isOpen()).isTrue();
        assertThat(latch.getCount()).isEqualTo(4);
    }
}
