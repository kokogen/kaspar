package net.koko.kaspar.config;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
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

@Configuration
public class KasparRedisConfig {

    @Value("${redisHost}")
    private String redisHost;

    @Value("${redisPort}")
    private int redisPort;

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


}
