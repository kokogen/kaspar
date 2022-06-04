package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.KasparTopicPartition;
import net.koko.kaspar.model.KasparTopicPartitionOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KasparStateStorageImpl implements StateStorage{
    public static Logger logger = LoggerFactory.getLogger(KasparStateStorageImpl.class);

    public static String STATE_STORAGE_KEY = "kafka-state-storage";

    @Autowired
    ReactiveRedisOperations<String, KasparTopicPartitionOffset> operations;

    @Override
    public Mono<Boolean> saveTopicPartitionOffset(KasparTopicPartitionOffset topicPartitionOffset) {
        return operations.opsForHash()
                .put(STATE_STORAGE_KEY, topicPartitionOffset.getTopicPartition().getKey(), topicPartitionOffset)
                .doOnError(e -> logger.error(e.getMessage()));
    }

    @Override
    public Mono<KasparTopicPartitionOffset> readOffset(KasparTopicPartition topicPartition) {
        return operations.opsForHash().get(STATE_STORAGE_KEY, topicPartition.getKey()).cast(KasparTopicPartitionOffset.class);
    }
}
