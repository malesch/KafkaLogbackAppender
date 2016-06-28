package com.calclab.kafka;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.Properties;

import static ch.qos.logback.core.CoreConstants.CODES_URL;
import static org.apache.kafka.common.config.SslConfigs.*;

/**
 * Base class holding all configuration parameters of the appender (the underlying KafkaProducer
 * and the appender itself).
 */
public abstract class KafkaLogbackConfigBase<I extends DeferredProcessingAware> extends UnsynchronizedAppenderBase<ILoggingEvent> {
    protected static final String TOPIC = null;
    private static final String BOOTSTRAP_SERVERS_CONFIG = ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
    private static final String COMPRESSION_TYPE_CONFIG = ProducerConfig.COMPRESSION_TYPE_CONFIG;
    private static final String ACKS_CONFIG = ProducerConfig.ACKS_CONFIG;
    private static final String RETRIES_CONFIG = ProducerConfig.RETRIES_CONFIG;
    private static final String KEY_SERIALIZER_CLASS_CONFIG = ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
    private static final String VALUE_SERIALIZER_CLASS_CONFIG = ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
    private static final String SECURITY_PROTOCOL = CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
    private static final String SSL_TRUSTSTORE_LOCATION = SSL_TRUSTSTORE_LOCATION_CONFIG;
    private static final String SSL_TRUSTSTORE_PASSWORD = SSL_TRUSTSTORE_PASSWORD_CONFIG;
    private static final String SSL_KEYSTORE_TYPE = SSL_KEYSTORE_TYPE_CONFIG;
    private static final String SSL_KEYSTORE_LOCATION = SSL_KEYSTORE_LOCATION_CONFIG;
    private static final String SSL_KEYSTORE_PASSWORD = SSL_KEYSTORE_PASSWORD_CONFIG;
    private static final String SASL_KERBEROS_SERVICE_NAME = SaslConfigs.SASL_KERBEROS_SERVICE_NAME;

    protected String brokerList = null;
    protected String topic = null;
    protected String compressionType = null;
    protected String keySerializerClass = null;
    protected String valueSerializerClass = null;
    protected String securityProtocol = null;
    protected String sslTruststoreLocation = null;
    protected String sslTruststorePassword = null;
    protected String sslKeystoreType = null;
    protected String sslKeystoreLocation = null;
    protected String sslKeystorePassword = null;
    protected String saslKerberosServiceName = null;
    protected String clientJaasConfPath = null;
    protected String kerb5ConfPath = null;

    protected int retries = 0;
    protected int requiredNumAcks = Integer.MAX_VALUE;
    protected boolean syncSend = false;

    protected Encoder<ILoggingEvent> encoder;

    /**
     * Return the configuration properties for the KafkaProducer.
     * @return Properties with configured producer params
     */
    protected Properties getProducerProperties() {
        // check for config parameter validity
        Properties props = new Properties();
        if (brokerList != null)
            props.put(BOOTSTRAP_SERVERS_CONFIG, brokerList);
        if (props.isEmpty())
            throw new ConfigException("The bootstrap servers property should be specified");
        if (topic == null)
            throw new ConfigException("Topic must be specified by the Kafka Logback appender");
        if (compressionType != null)
            props.put(COMPRESSION_TYPE_CONFIG, compressionType);
        if (requiredNumAcks != Integer.MAX_VALUE)
            props.put(ACKS_CONFIG, Integer.toString(requiredNumAcks));
        if (retries > 0)
            props.put(RETRIES_CONFIG, retries);
        if (securityProtocol != null) {
            props.put(SECURITY_PROTOCOL, securityProtocol);
        }
        if (securityProtocol != null && securityProtocol.contains("SSL") && sslTruststoreLocation != null &&
                sslTruststorePassword != null) {
            props.put(SSL_TRUSTSTORE_LOCATION, sslTruststoreLocation);
            props.put(SSL_TRUSTSTORE_PASSWORD, sslTruststorePassword);

            if (sslKeystoreType != null && sslKeystoreLocation != null &&
                    sslKeystorePassword != null) {
                props.put(SSL_KEYSTORE_TYPE, sslKeystoreType);
                props.put(SSL_KEYSTORE_LOCATION, sslKeystoreLocation);
                props.put(SSL_KEYSTORE_PASSWORD, sslKeystorePassword);
            }
        }
        if (securityProtocol != null && securityProtocol.contains("SASL") && saslKerberosServiceName != null && clientJaasConfPath != null) {
            props.put(SASL_KERBEROS_SERVICE_NAME, saslKerberosServiceName);
            System.setProperty("java.security.auth.login.config", clientJaasConfPath);
            if (kerb5ConfPath != null) {
                System.setProperty("java.security.krb5.conf", kerb5ConfPath);
            }
        }
        if (keySerializerClass != null) {
            props.put(KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        } else {
            props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }
        if (valueSerializerClass != null) {
            props.put(VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        } else {
            props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        }

        return props;
    }

    // Producer parameters

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }

    public void setRequiredNumAcks(int requiredNumAcks) {
        this.requiredNumAcks = requiredNumAcks;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }


    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setSyncSend(boolean syncSend) {
        this.syncSend = syncSend;
    }

    public void setKeySerializerClass(String clazz) { this.keySerializerClass = clazz; }

    public void setValueSerializerClass(String clazz) { this.valueSerializerClass = clazz; }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public void setSslTruststoreLocation(String sslTruststoreLocation) {
        this.sslTruststoreLocation = sslTruststoreLocation;
    }

    public void setSslTruststorePassword(String sslTruststorePassword) {
        this.sslTruststorePassword = sslTruststorePassword;
    }

    public void setSslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    public void setSslKeystoreType(String sslKeystoreType) {
        this.sslKeystoreType = sslKeystoreType;
    }

    public void setSslKeystoreLocation(String sslKeystoreLocation) {
        this.sslKeystoreLocation = sslKeystoreLocation;
    }

    public void setSaslKerberosServiceName(String saslKerberosServiceName) {
        this.saslKerberosServiceName = saslKerberosServiceName;
    }

    public void setClientJaasConfPath(String clientJaasConfPath) {
        this.clientJaasConfPath = clientJaasConfPath;
    }

    public void setKerb5ConfPath(String kerb5ConfPath) {
        this.kerb5ConfPath = kerb5ConfPath;
    }

    // Appender configuration parameters

    public void setLayout(Layout<ILoggingEvent> layout) {
        addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        addWarn("See also " + CODES_URL + "#layoutInsteadOfEncoder for details");
        LayoutWrappingEncoder<ILoggingEvent> lwe = new LayoutWrappingEncoder<ILoggingEvent>();
        lwe.setLayout(layout);
        lwe.setContext(context);
        this.encoder = lwe;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) { this.encoder = encoder; }
}
