package net.koko.kaspar.service.kafka;

import net.koko.kaspar.model.state.KasparTopicPartition;
import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import net.koko.kaspar.service.storage.DataStorage;
import net.koko.kaspar.service.storage.StateStorage;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class KasparReceiver implements CommandLineRunner {
    public static Logger logger = LoggerFactory.getLogger(KasparReceiver.class);
    @Autowired
    DataStorage storage;

    StateStorage stateStorage;

    ReceiverOptions<String, KasparItem> receiverOptions;

    @Value(value = "${topic}")
    String topic;

    @Autowired
    public KasparReceiver(ReceiverOptions<String, KasparItem> receiverOptions, StateStorage _stateStorage) {
        this.stateStorage = _stateStorage;
        this.receiverOptions = receiverOptions;
    }

    @PostConstruct
    void init() throws Exception {
        stateStorage.readOffset(topic).collectList().subscribe(offsetList ->

            this.receiverOptions.addAssignListener(partitions -> partitions.forEach(partition -> {

                long offset = Objects.requireNonNull(offsetList).stream()
                        .filter(a -> a.getTopicPartition().getPartition() == partition.topicPartition().partition())
                        .map(KasparTopicPartitionOffset::getOffset).findFirst()
                        .get();
                partition.seek(offset);
                logger.info("******* init(): " + topic + ":" + partition.position() + ":" + offset);
            }))
        );
    }

    @Override
    public void run(String... args) throws Exception {
/*
        KafkaReceiver.create(receiverOptions).doOnConsumer(
                c -> {

                    c.committed(new HashSet<>(Objects.requireNonNull(receiverOptions.assignment())))
                            .forEach((key, value) -> System.out.printf("$$$$$$$$$$$$ part: %s, offs: %d%n", key.partition(), value.offset()));
                    return Mono.empty();

                }
        ).subscribe();

*/
        KafkaReceiver.create(receiverOptions)
                .receive()
                .doOnNext(record -> logger.info("blink> " + record.partition() + "::" +record.receiverOffset().offset()))
                .doOnError(e -> logger.error(e.getMessage()))
                .subscribe(record -> {
                            KasparTopicPartitionOffset kasparTopicPartitionOffset = new KasparTopicPartitionOffset(new KasparTopicPartition(record.topic(), record.partition()), record.offset());
                            storage.save(kasparTopicPartitionOffset, record.key(), record.value());
                            stateStorage.saveTopicPartitionOffset(kasparTopicPartitionOffset);
                            record.receiverOffset().commit();
                        });
    }
}
