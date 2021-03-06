Kafka Logback Appender
======================

A simple Logback appender which sends the logging output to a [Kafka] (http://kafka.apache.org) broker.

This implementation is inspired by the log4j appender included in the Kafka source distribution.<br/>
See e.g. [KafkaLog4jAppender] (https://github.com/apache/kafka/blob/trunk/log4j-appender/src/main/java/org/apache/kafka/log4jappender/KafkaLog4jAppender.java).

The Kafka Logback Appender is based on the Kafka version `0.10.0.0`.

## Configuration parameters

### Appender parameters
<dl>
  <dt>topic</dt>
  <dd>The Kafka topic the message should be published to.</dd>

  <dt>SyncSend</dt>
  <dd>Send message synchronously (if true, wait for completion of the returned Future).</dd>
</dl>

### Kafka producer parameters

The appender takes the available Kafka Producer parameters (see [ProductConfig] (https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/clients/producer/ProducerConfig.java) class).

<dl>
  <dt>BrokerList &nbsp; (<small></i><code>bootstrap.servers</code></i></small>)</dt>
  <dd>List of host/port broker addresses. E.g.: <code>host1:port1, host2:port2,...</code>.</dd>

  <dt>CompressionType &nbsp; (<small></i><code>compression.type</code></i></small>)</dt>
  <dd>The compression type for all data generated by the producer. The default is none (i.e. no compression). Valid  values are <code>none</code>, <code>gzip</code>, <code>snappy</code>, or <code>lz4</code>. Compression is of full batches of data, so the efficacy of batching will also impact the compression ratio (more batching means better compression).</dd>

  <dt>RequiredNumAcks &nbsp; (<small></i><code>acks</code></i></small>)</dt>
  <dd>The number of acknowledgments the producer requires the leader to have received before considering a request complete. This controls the  durability of records that are sent. The following settings are common:  <ul> <li><code>acks=0</code> If set to zero then the producer will not wait for any acknowledgment from the server at all. The record will be immediately added to the socket buffer and considered sent. No guarantee can be made that the server has received the record in this case, and the <code>retries</code> configuration will not take effect (as the client won\'t generally know of any failures). The offset given back for each record will always be set to -1. <li><code>acks=1</code> This will mean the leader will write the record to its local log but will respond without awaiting full acknowledgement from all followers. In this case should the leader fail immediately after acknowledging the record but before the followers have replicated it then the record will be lost. <li><code>acks=-1</code> This means the leader will wait for the full set of in-sync replicas to acknowledge the record. This guarantees that the record will not be lost as long as at least one in-sync replica remains alive. This is the strongest available guarantee.</ul></dd>

  <dt>Retries &nbsp; (<small></i><code>retries</code></i></small>)</dt>
  <dd>Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially transient error. Note that this retry is no different than if the client resent the record upon receiving the error. Allowing retries will potentially change the ordering of records because if two records are sent to a single partition, and the first fails and is retried but the second succeeds, then the second record may appear first.</dd>

  <dt>KeySerializerClass &nbsp; (<small></i><code>key.serializer</code></i></small>)</dt>
  <dd>Serializer class for key that implements the <code>Serializer</code> interface. Default: <code>org.apache.kafka.common.serialization.StringSerializer</code></dd>

  <dt>ValueSerializerClass &nbsp; (<small></i><code>value.serializer</code></i></small>)</dt>
  <dd>Serializer class for value that implements the <code>Serializer</code> interface.Default: <code>org.apache.kafka.common.serialization.ByteArraySerializer</code></dd>

  <dt>SecurityProtocol &nbsp; (<small></i><code>security.protocol</code></i></small>)</dt>
  <dd>Protocol used to communicate with brokers. Valid values are <i>(on this machine)</i>: <code>PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL</code>.</dd>

  <dt>SslTruststoreLocation &nbsp; (<small></i><code>ssl.truststore.location</code></i></small>)</dt>
  <dd>The location of the trust store file.</dd>

  <dt>SslTruststorePassword &nbsp; (<small></i><code>ssl.truststore.password</code></i></small>)</dt>
  <dd>The password for the trust store file.</dd>

  <dt>SslKeystoreType &nbsp; (<small></i><code>ssl.keystore.type</code></i></small>)</dt>
  <dd>The file format of the key store file. This is optional for client.</dd>

  <dt>SslKeystoreLocation &nbsp; (<small></i><code>ssl.keystore.location</code></i></small>)</dt>
  <dd>The location of the key store file. This is optional for client and can be used for two-way authentication for client.</dd>

  <dt>SslKeystorePassword &nbsp; (<small></i><code>ssl.keystore.password</code></i></small>)</dt>
  <dd>The store password for the key store file.This is optional for client and only needed if ssl.keystore.location is configured.</dd>

  <dt>SaslKerberosServiceName &nbsp; (<small></i><code>sasl.kerberos.service.name</code></i></small>)</dt>
  <dd>The Kerberos principal name that Kafka runs as. This can be defined either in Kafka's JAAS config or in Kafka's config.</dd>

</dl>


## Example usage

```xml
<configuration>
    <appender name="kafka-appender" class="com.calclab.kafka.KafkaLogbackAppender">
        <BrokerList>localhost:9092</BrokerList>
        <Topic>log</Topic>
        <SyncSend>false</SyncSend>

        <encoder>
          <pattern>%date{yyyy-MM-dd'T'HH:mm:ss.SSS'Z', UTC} %-5p [%c{1}] %m</pattern>
          <charset>UTF-8</charset>
        </encoder>
    </appender>

   <root level="INFO">
    <appender-ref ref="kafka-appender" />
  </root>
</configuration>
```

## License

Copyright © 2016 Marcus Spiegel

Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided that the above notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.


