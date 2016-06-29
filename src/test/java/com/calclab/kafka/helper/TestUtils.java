package com.calclab.kafka.helper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.Encoder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.LoggerFactory;

import java.util.*;


public class TestUtils {
    public static final String KAFKA_APPENDER = "kafka";

    /**
     * Return a Logger with a configured KafkaLogbackAppender.
     * @param topic Topic for the published messages
     * @param ackDelay The message processing delay of the KafkaProducer
     * @param syncSend SyncSend flag of the KafkaLogbackAppender
     * @param encoder Encoder instance for the KafkaLogbackAppender (overrides a layout if specified)
     * @param layout Layout instance (deprecated) for the KafkaLogbackAppender
     * @return Configured logger with KafkaLogbackAppender
     */
    public static Logger createKafkaLogger(String topic, long ackDelay, boolean syncSend, Encoder encoder, Layout layout) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();
        lc.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.OFF);

        InspectableKafkaLogbackAppender kafkaAppender = new InspectableKafkaLogbackAppender(null);
        kafkaAppender.setName(KAFKA_APPENDER);
        kafkaAppender.setContext(lc);
        kafkaAppender.setTopic(topic);
        kafkaAppender.setSyncSend(syncSend);
        if (layout != null) {
            layout.setContext(lc);
            layout.start();
            kafkaAppender.setLayout(layout);
        }
        if (encoder != null) {
            encoder.setContext(lc);
            encoder.start();
            kafkaAppender.setEncoder(encoder);
        }
        kafkaAppender.start();
        kafkaAppender.getMockProducer().setAckDelay(ackDelay);
;

        Logger logger = (Logger) LoggerFactory.getLogger(KAFKA_APPENDER);
        logger.addAppender(kafkaAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);

        return logger;
    }

    /**
     * Return a Logger with a configured KafkaLogbackAppender.
     * @param topic Topic for the published messages
     * @param ackDelay The message processing delay of the KafkaProducer
     * @param syncSend SyncSend flag of the KafkaLogbackAppender
     * @return Configured logger with KafkaLogbackAppender
     */
    public static Logger createKafkaLogger(String topic, long ackDelay, boolean syncSend) {
        return createKafkaLogger(topic, ackDelay, syncSend, null, null);
    }

    /**
     * Return the list of messages sent to Kafka as a map, keyed by the published topic.
     * @param logger The logger with KafkaLogbackAppender
     * @return Map with messages or null, if no Kafka logger was passed
     */
    public static HashMap<String, ArrayList<String>> getQueuedMessages(Logger logger) {
        InspectableKafkaLogbackAppender kafkaAppender = (InspectableKafkaLogbackAppender)logger.getAppender(KAFKA_APPENDER);
        if (kafkaAppender != null) {
            ArrayList events = new ArrayList<String>();
            String topic = kafkaAppender.getMockProducer().getTopic();

            List<ProducerRecord> queue = kafkaAppender.getMockProducer().getQueue();
            for (ProducerRecord<String, byte[]> rec : queue) {
                events.add(new String(rec.value()));
            }

            HashMap<String, ArrayList<String>> kafkaMessages = new HashMap<String, ArrayList<String>>();
            kafkaMessages.put(topic, events);
            return kafkaMessages;
        }
        return null;
    }

    /**
     * Return the sent message Strings of a specific topic.
     * @param logger The logger with KafkaLogbackAppender
     * @param topic Topic
     * @return List of message string or null for a non-existent topic or KafkaLogbackAppender queue
     */
    public static ArrayList<String> getTopicQueueMessages(Logger logger, String topic) {
        HashMap<String, ArrayList<String>> queues = getQueuedMessages(logger);
        if (queues != null) {
            return queues.get(topic);
        }
        return null;
    }
}
