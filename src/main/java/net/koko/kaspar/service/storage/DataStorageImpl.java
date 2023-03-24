package net.koko.kaspar.service.storage;

import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class DataStorageImpl implements DataStorage{
    public static Logger logger = LoggerFactory.getLogger(DataStorageImpl.class);
/*
    @Autowired
    JdbcTemplate jdbcTemplate;
*/
    @Override
    public void save(KasparTopicPartitionOffset kasparTopicPartitionOffset, String key, KasparItem kasparItem) {
        logger.info(String.format("topic: %s, partition: %d, offset: %d, key: %s, item: %s",
                kasparTopicPartitionOffset.getTopicPartition().getTopic(),
                kasparTopicPartitionOffset.getTopicPartition().getPartition(),
                kasparTopicPartitionOffset.getOffset(),
                key,
                kasparItem.toString()));

        //saveToDb(kasparTopicPartitionOffset, key, kasparItem);
    }
/*
    void saveToDb(KasparTopicPartitionOffset kasparTopicPartitionOffset, String key, KasparItem kasparItem){
        String sql = "INSERT INTO KASPAR.ITEM(KEY, MSG, TOPIC, PARTITION, OFFSET) VALUES(?, ?, ?, ?, ?);";
        final int updateCnt = jdbcTemplate.update(sql,
                kasparItem.getKey(),
                kasparItem.getMsg(),
                kasparTopicPartitionOffset.getTopicPartition().getTopic(),
                kasparTopicPartitionOffset.getTopicPartition().getPartition(),
                kasparTopicPartitionOffset.getOffset()
        );
        logger.info("{} rows affected", updateCnt);
    }

 */
}
