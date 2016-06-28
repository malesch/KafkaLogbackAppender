package com.calclab.kafka;


import ch.qos.logback.classic.spi.ILoggingEvent;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaLogbackAppender extends KafkaLogbackConfigBase<ILoggingEvent> {

    protected Producer producer = null;

    public void start() {
        super.start();
        Properties props = getProducerProperties();
        this.producer = createKafkaProducer(props);
        addInfo("Kafka producer connected to " + brokerList);
        addInfo("Logging for topic: " + topic);
    }

    @Override
    public void stop() {
        super.stop();
        if (producer != null) {
            producer.close();
        }
    }

    protected void append(ILoggingEvent event) {
        byte[] message = null;
        if (encoder != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                encoder.init(baos);
                encoder.setContext(getContext());
                encoder.doEncode(event);
                message = baos.toByteArray();
            } catch (IOException ex) {
                addError("Error encoding event", ex);
            }
        } else {
            message = event.getMessage().getBytes();
        }
        Future<RecordMetadata> response = producer.send(new ProducerRecord<byte[], byte[]>(topic, message));
        if (syncSend) {
            try {
                response.get();
            } catch (Exception ex) {
                addError("Error waiting for Kafka response", ex);
            }
        }
    }

    protected Producer createKafkaProducer(Properties props) {
        return new KafkaProducer<byte[], byte[]>(props);
    }
}
