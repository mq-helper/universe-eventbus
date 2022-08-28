package org.mqhelper.eventbus;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mqhelper.eventbus.impl.DefaultLocalSubscriberRegistry;
import org.mqhelper.eventbus.kafka.KafkaEventPublisher;
import org.mqhelper.eventbus.kafka.KafkaMessageConsumerRegistry;

/**
 * @author SongyangJi
 * @date 2022/08/28
 */
public class FirstVersionTest {

    final String BOOTSTRAP_SERVERS = "localhost:9092";
    Properties consumerProps;
    Properties producerProps;

    @Before
    public void setUp() throws Exception {
        consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS); // todo
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "helloEventListener");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    @Test
    public void testPublisher() {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            KafkaEventPublisher eventPublisher = new KafkaEventPublisher(producer);
            for (;;) {
                eventPublisher.publish(new HelloEvent("hello from v1.0"));
                //Thread.sleep(500);
            }
        } catch (Exception e) {
        }
    }

    @Test
    public void testSubscriber() throws InterruptedException {
        LocalSubscriberRegistry localSubscriberRegistry = new DefaultLocalSubscriberRegistry();
        // BeanPostProcessor
        localSubscriberRegistry.register(new HelloEventListener());

        // @Bean
        KafkaMessageConsumerRegistry kafkaMessageConsumerRegistry = new KafkaMessageConsumerRegistry(consumerProps,
            localSubscriberRegistry);

        // invoke when context container refresh
        kafkaMessageConsumerRegistry.registerKafkaMessageConsumer(
            localSubscriberRegistry.listAllEventMessageConsumers());

        Thread.sleep(100000000); //
    }

}
