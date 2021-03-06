package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class KasparStateStorageImpl implements StateStorage{
    public static Logger logger = LoggerFactory.getLogger(KasparStateStorageImpl.class);

    public static String STATE_STORAGE_KEY = "kafka-state-storage";

    @Autowired
    ReactiveRedisOperations<String, KasparTopicPartitionOffset> operations;

    @Override
    public Mono<Boolean> saveTopicPartitionOffset(KasparTopicPartitionOffset topicPartitionOffset) {
        return operations.opsForHash()
                .put(STATE_STORAGE_KEY,
                        topicPartitionOffset.getTopicPartition().getKey(), topicPartitionOffset)
                .doOnError(e -> logger.error(e.getMessage())
                );
    }

    @Override
    public Flux<KasparTopicPartitionOffset> readOffset(String topic) {
        Optional<List<Object>> keysOpt = operations.opsForHash().keys(topic + "-*").collectList().blockOptional();
        if(!keysOpt.isPresent()) return Flux.empty();
        return operations.opsForHash()
                .multiGet(STATE_STORAGE_KEY, keysOpt.get())
                .flatMapMany(lst -> Flux.fromArray(lst.toArray()))
                .cast(KasparTopicPartitionOffset.class);
    }
}
