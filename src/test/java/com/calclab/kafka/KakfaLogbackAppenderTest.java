package com.calclab.kafka;

import ch.qos.logback.classic.Logger;
import com.calclab.kafka.helper.TestUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class KakfaLogbackAppenderTest extends TestCase {

    private ArrayList<String> buildTestMessages(int count) {
        ArrayList<String> messages = new ArrayList<String>();
        for (int i=0; i<count; i++) {
            messages.add("Log message: "+i);
        }
        return messages;
    }

    private void waitMilliseconds(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // continue
        }
    }

    @Test
    public void testImmediateAck() {
        String topic = "ImmediateAck";
        ArrayList<String> testMessages = buildTestMessages(200);
        Logger logger = TestUtils.createKafkaLogger(topic, 0, false);
        for (String message : testMessages) {
            logger.info(message);
        }

        ArrayList<String> sentMessages = TestUtils.getTopicQueueMessages(logger, topic);
        Assert.assertEquals(testMessages, sentMessages);
    }

    @Test
    public void testDelayedAck() {
        String topic = "DelayedAck";
        ArrayList<String> testMessages = buildTestMessages(5);

        Logger logger = TestUtils.createKafkaLogger(topic, 1000, false);
        for (String message : testMessages) {
            logger.info(message);
        }

        ArrayList<String> sentMessages1 = TestUtils.getTopicQueueMessages(logger, topic);
        Assert.assertNotEquals(testMessages, sentMessages1);

        // Wait for producer to complete sending messages
        waitMilliseconds(5000);

        ArrayList<String> sentMessages2 = TestUtils.getTopicQueueMessages(logger, topic);
        Assert.assertEquals(testMessages, sentMessages2);
    }

    @Test
    public void testSyncSendDelayedAck() {
        String topic = "SyncSendDelayedAck";
        ArrayList<String> testMessages = buildTestMessages(5);

        Logger logger = TestUtils.createKafkaLogger(topic, 1000, true);
        for (String message : testMessages) {
            logger.info(message);
        }

        ArrayList<String> sentMessages = TestUtils.getTopicQueueMessages(logger, topic);
        Assert.assertEquals(testMessages, sentMessages);
    }
}
