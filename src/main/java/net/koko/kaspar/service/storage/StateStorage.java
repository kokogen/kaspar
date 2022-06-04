package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.KasparTopicPartition;
import net.koko.kaspar.model.KasparTopicPartitionOffset;
import reactor.core.publisher.Mono;

public interface StateStorage {
    Mono<Boolean> saveTopicPartitionOffset(KasparTopicPartitionOffset topicPartitionOffset);
    Mono<KasparTopicPartitionOffset> readOffset(KasparTopicPartition topicPartition);
}
