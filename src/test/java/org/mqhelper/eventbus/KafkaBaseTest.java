package org.mqhelper.eventbus;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

/**
 * @author SongyangJi
 * @date 2022/08/21
 */
public class KafkaBaseTest extends TestCase {
    String BOOTSTRAP_SERVERS =
        "localhost:9092";
    String topic = "test";

    @Test
    public void testConsumeMessage() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
            "testConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class.getName());

        // Create the consumer using consumerProps.
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Subscribe to the topic.
            consumer.subscribe(Collections.singletonList(topic));
            for (; ; ) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                System.out.println("process messages");
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("topic=%s ", record.topic());
                    System.out.printf("key=%s ", record.key());
                    System.out.printf("value=%s ", record.value());
                    System.out.printf("headers=%s ", record.headers().toString());
                    System.out.printf("offset=%d ", record.offset());
                    System.out.printf("partition=%d ", record.partition());
                    System.out.printf("timestamp=%d \n", record.timestamp());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testProduceMessage() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(properties);

        try {
            int i = 0;
            Random random = new Random();
            for (; ; ++i) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, "demo-value" + i);
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.println(metadata);
                    } else {
                        if (exception instanceof RetriableException) {
                            System.err.printf("可重试异常:%s\n", exception);
                        } else {
                            System.err.printf("不可重试异常:%s\n", exception);
                        }
                    }
                });
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            }
        } finally {
            producer.close();
        }
    }
}