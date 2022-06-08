package net.koko.kaspar.config;


import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverPartition;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KasparConfig {

    @Value("${redisHost}")
    private String redisHost;

    @Value("${redisPort}")
    private int redisPort;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(redisHost);
        redisStandaloneConfig.setPort(redisPort);
        return new LettuceConnectionFactory(redisStandaloneConfig);
    }

    @Bean
    public ReactiveRedisOperations<String, KasparTopicPartitionOffset> redisOperations(LettuceConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<KasparTopicPartitionOffset> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(KasparTopicPartitionOffset.class);

        RedisSerializationContext<String, KasparTopicPartitionOffset> serializationContext = RedisSerializationContext
                .<String, KasparTopicPartitionOffset>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .hashKey(new StringRedisSerializer())
                .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public ReceiverOptions<String, KasparItem> receiverOptions(@Value(value = "${topic}") String topic) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kaspar-grp");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<String, KasparItem> options =
                ReceiverOptions.<String, KasparItem>create(props)
                        .addAssignListener(partitions -> partitions.forEach(ReceiverPartition::seekToEnd))
                        .subscription(Collections.singleton(topic));

        options.commitInterval(Duration.ZERO);
        options.commitBatchSize(0);
        return options.subscription(Collections.singletonList(topic));
    }

    @Bean
    public KafkaReceiver<String, KasparItem> kafkaReceiver(ReceiverOptions<String, KasparItem> receiverOptions) {
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public KafkaSender<String, KasparItem> kafkaSender() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        SenderOptions<String, KasparItem> senderOptions = SenderOptions.create(producerProps);

        return KafkaSender.create(senderOptions);
    }
}
