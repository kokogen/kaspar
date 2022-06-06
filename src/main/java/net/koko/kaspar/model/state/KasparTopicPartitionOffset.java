package net.koko.kaspar.model.state;

public record KasparTopicPartitionOffset(KasparTopicPartition topicPartition, long offset) {
    public KasparTopicPartition getTopicPartition() {
        return topicPartition;
    }

    @Override
    public long offset() {
        return offset;
    }
}
