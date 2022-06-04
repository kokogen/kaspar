package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.KasparTopicPartitionOffset;
import net.koko.kaspar.model.dto.KasparItem;

public interface DataStorage {
    void save(KasparTopicPartitionOffset kasparTopicPartitionOffset, KasparItem kasparItem);
}
