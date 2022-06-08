package net.koko.kaspar.model.state;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KasparTopicPartition {
    String topic;
    int partition;
    public String getKey() {
        return topic + "-" + partition;
    }
}
