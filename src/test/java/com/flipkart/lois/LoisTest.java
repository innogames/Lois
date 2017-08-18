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

package com.flipkart.lois;

import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.impl.BufferedChannel;
import com.flipkart.lois.channel.impl.SimpleChannel;
import com.flipkart.lois.routine.Routine;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class LoisTest {

    private class SampleRoutine implements Routine {

        Channel<String> channel;

        private SampleRoutine(Channel<String> channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            channel.send("dude");
        }
    }


    @Test
    public void start_go_routine() throws Exception {
        //given
        Channel<String> stringChannel = new SimpleChannel<>();
        SampleRoutine sampleRoutine = new SampleRoutine(stringChannel);
        SampleRoutine sampleRoutine1 = new SampleRoutine(stringChannel);
        //when
        Lois.go(sampleRoutine, sampleRoutine1);
        //then
        assertThat(stringChannel.receive()).isEqualTo("dude");
        assertThat(stringChannel.receive()).isEqualTo("dude");
    }

    @Test
    public void send_message_from_one_source_to_multiple_destinations() throws Exception {
        //given
        Channel<String> sendChannel = new BufferedChannel<>(2);
        Channel<String> receiveChannel1 = new SimpleChannel<>();
        Channel<String> receiveChannel2 = new SimpleChannel<>();
        //when
        Lois.mux(sendChannel, receiveChannel1, receiveChannel2);
        receiveChannel1.send("ch1");
        receiveChannel2.send("ch2");

        //then
        assertThat(sendChannel.receive()).isEqualTo("ch1");
        assertThat(sendChannel.receive()).isEqualTo("ch2");

        receiveChannel1.close();
        receiveChannel2.close();

        assertThat(sendChannel.isOpen()).isTrue();
    }

    @Test
    public void split_one_channel_into_multiple() throws Exception {
        //given
        Channel<String> sink1 = new BufferedChannel<>(2);
        Channel<String> sink2 = new BufferedChannel<>(2);
        Channel<String> channel = new BufferedChannel<>(1);

        //when
        Lois.deMux(channel, sink1, sink2);
        channel.send("I");
        channel.send("Am");
        channel.send("The");
        channel.send("Champion");
        channel.send("My");
        channel.send("Friend");
        channel.send("!");

        int sink1count = 0;
        int sink2count = 0;

        while (sink1.hasNext()) {
            sink1.receive();
            sink1count++;
            Thread.sleep(1);
        }
        while (sink2.hasNext()) {
            sink2.receive();
            sink2count++;
            Thread.sleep(1);
        }

        //then
        assertThat(sink1count + sink2count).isEqualTo(7);
    }

    @Test
    public void copy_from_one_source_to_multiple_destinations() throws Exception {
        //given
        Channel<String> sink1 = new BufferedChannel<>(2);
        Channel<String> sink2 = new BufferedChannel<>(2);
        Channel<String> channel = new BufferedChannel<>(1);

        //when
        Lois.multiCast(channel, sink1, sink2);
        channel.send("I");
        channel.send("Am");
        channel.send("The");
        channel.send("Champion");

        int sink1count = 0;
        int sink2count = 0;

        while (sink1.hasNext()) {
            if (sink1count == 0) {
                assertThat(sink1.receive()).isEqualTo("I");
            } else {
                sink1.receive();
            }
            Thread.sleep(1);
            sink1count++;
        }
        //count is 3 because sink2 is blocked at 3 and the next element in source can't be processed yet
        assertThat(sink1count).isEqualTo(3);

        while (sink2.hasNext()) {
            if (sink2count == 0) {
                assertThat(sink2.receive()).isEqualTo("I");
            } else {
                sink2.receive();
            }
            Thread.sleep(1);
            sink2count++;
        }
        assertThat(sink2count).isEqualTo(4);

        assertThat(sink1.receive()).isEqualTo("Champion");
    }
}
