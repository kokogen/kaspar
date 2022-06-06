package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StateStorage {
    Mono<Boolean> saveTopicPartitionOffset(KasparTopicPartitionOffset topicPartitionOffset);
    Flux<KasparTopicPartitionOffset> readOffset(String topic);
}
