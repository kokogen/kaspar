package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;

public interface DataStorage {
    void save(KasparTopicPartitionOffset kasparTopicPartitionOffset, String key, KasparItem kasparItem);
}
