package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.KasparTopicPartitionOffset;
import net.koko.kaspar.model.dto.KasparItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataStorageImpl implements DataStorage{
    public static Logger logger = LoggerFactory.getLogger(DataStorageImpl.class);
    @Override
    public void save(KasparTopicPartitionOffset kasparTopicPartitionOffset, KasparItem kasparItem) {
        logger.info(String.format("topic: %s, partition: %d, offset: %d, item: %s",
                kasparTopicPartitionOffset.getTopicPartition().topic(),
                kasparTopicPartitionOffset.getTopicPartition().partition(),
                kasparTopicPartitionOffset.offset(),
                kasparItem.toString()));
    }
}
