package net.koko.kaspar.model.state;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KasparTopicPartitionOffset implements Serializable {
    KasparTopicPartition topicPartition;
    long offset;
}
