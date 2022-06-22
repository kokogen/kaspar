package net.koko.kaspar.model.state;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class KasparTopicPartitionOffset implements Serializable {
    KasparTopicPartition topicPartition;
    long offset;

    public KasparTopicPartitionOffset(){}
}
