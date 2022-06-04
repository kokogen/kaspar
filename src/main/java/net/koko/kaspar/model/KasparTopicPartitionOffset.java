package net.koko.kaspar.model;

public record KasparTopicPartitionOffset(KasparTopicPartition topicPartition, long offset) {
    public KasparTopicPartition getTopicPartition() {
        return topicPartition;
    }

    @Override
    public long offset() {
        return offset;
    }
}
