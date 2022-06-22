package net.koko.kaspar.model.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
public class KasparTopicPartition implements Serializable {
    String topic;
    int partition;

    public KasparTopicPartition(){}

    public KasparTopicPartition(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
    }
}
