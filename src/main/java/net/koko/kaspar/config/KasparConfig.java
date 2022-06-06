package net.koko.kaspar.config;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
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
    public ReceiverOptions<String, KasparItem> kafkaOptions(@Value(value = "${topic}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, KasparItem> basicOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        basicOptions.commitInterval(Duration.ZERO);
        basicOptions.commitBatchSize(0);
        return basicOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, KasparItem> reactiveKafkaConsumerTemplate(ReceiverOptions<String, KasparItem> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, KasparItem> reactiveKafkaProducerTemplate() {
        //Map<String, Object> props = properties.buildProducerProperties();
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new JsonSerializer<KasparItem>());

        SenderOptions<String, KasparItem> senderOptions =
                SenderOptions.<String, KasparItem>create(producerProps);
        //return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

}
