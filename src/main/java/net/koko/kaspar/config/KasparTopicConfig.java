package net.koko.kaspar.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KasparTopicConfig {
    @Value("${topic}")
    String topic;


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    NewTopic newTopic(){
        return TopicBuilder.name(topic).partitions(2).replicas(1).build();
    }

    @Bean
    KafkaAdmin kafkaAdmin(){
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(props);
    }
}
