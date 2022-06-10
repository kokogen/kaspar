package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DataStorageImpl implements DataStorage{
    public static Logger logger = LoggerFactory.getLogger(DataStorageImpl.class);
    @Override
    public void save(KasparTopicPartitionOffset kasparTopicPartitionOffset, String key, KasparItem kasparItem) {
        logger.info(String.format("topic: %s, partition: %d, offset: %d, key: %s, item: %s",
                kasparTopicPartitionOffset.getTopicPartition().getTopic(),
                kasparTopicPartitionOffset.getTopicPartition().getPartition(),
                kasparTopicPartitionOffset.getOffset(),
                key,
                kasparItem.toString()));
    }
}
