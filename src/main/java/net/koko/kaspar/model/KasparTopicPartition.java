package net.koko.kaspar.model;

public record KasparTopicPartition(String topic, int partition) {
    public String getKey() {
        return topic + "-" + partition;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public int partition() {
        return partition;
    }
}
