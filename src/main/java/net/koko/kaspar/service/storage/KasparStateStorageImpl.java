package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartition;
import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class KasparStateStorageImpl implements StateStorage{
    public static Logger logger = LoggerFactory.getLogger(KasparStateStorageImpl.class);

    public static String STATE_STORAGE_KEY = "kafka-state-storage";

    public static String getKey(KasparTopicPartition kasparTopicPartition){
        return kasparTopicPartition.getTopic() + "-" + kasparTopicPartition.getPartition();
    }

    @Autowired
    ReactiveRedisOperations<String, KasparTopicPartitionOffset> operations;

    @Override
    public void saveTopicPartitionOffset(KasparTopicPartitionOffset topicPartitionOffset) {
        operations.opsForHash()
                .put(STATE_STORAGE_KEY,
                        getKey(topicPartitionOffset.getTopicPartition()), topicPartitionOffset)
                .doOnError(e -> logger.error(e.getMessage())
                ).subscribe();
    }

    @Override
    public Flux<KasparTopicPartitionOffset> readOffset(String topic) throws Exception{

        Mono<List<Object>> keys = operations.opsForHash().keys(STATE_STORAGE_KEY).collectList();

        return keys.flatMapMany(keysMono ->
                operations.opsForHash().multiGet(STATE_STORAGE_KEY, keysMono)
                        .flatMapMany(Flux::fromIterable)
                        .cast(KasparTopicPartitionOffset.class)
        );
/*
        List<Object> keys = operations.opsForHash().keys(STATE_STORAGE_KEY).collectList().block();
        return operations.opsForHash()
                .multiGet(STATE_STORAGE_KEY, keys)
                .flatMapMany(Flux::fromIterable)
                .cast(KasparTopicPartitionOffset.class);
*/
        //return keys.flatMapMany(Flux::fromIterable).map(k -> operations.opsForHash().get(STATE_STORAGE_KEY, k).cast(KasparTopicPartitionOffset.class).block());
    }


}
