package net.koko.kaspar.model.state;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KasparTopicPartitionOffset {
    KasparTopicPartition topicPartition;
    long offset;

    public KasparTopicPartition getTopicPartition() {
        return topicPartition;
    }

    public long offset() {
        return offset;
    }
}
