package net.koko.kaspar.service.kafka;

import net.koko.kaspar.KasparApplication;
import net.koko.kaspar.model.KasparTopicPartition;
import net.koko.kaspar.model.KasparTopicPartitionOffset;
import net.koko.kaspar.model.dto.KasparItem;
import net.koko.kaspar.service.storage.DataStorage;
import net.koko.kaspar.service.storage.StateStorage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class KasparConsumer implements CommandLineRunner {
    public static Logger logger = LoggerFactory.getLogger(KasparConsumer.class);
    @Autowired
    ReactiveKafkaConsumerTemplate<String, KasparItem> consumer;
    @Autowired
    DataStorage storage;

    @Autowired
    StateStorage stateStorage;

    Flux<KasparItem> process(){

        return consumer.receive()
                .doOnNext(record -> {
                    KasparTopicPartitionOffset kasparTopicPartitionOffset = new KasparTopicPartitionOffset(new KasparTopicPartition(record.topic(), record.partition()), record.offset());
                    storage.save(kasparTopicPartitionOffset, record.value());
                    stateStorage.saveTopicPartitionOffset(kasparTopicPartitionOffset).blockOptional();
                    record.receiverOffset().acknowledge();
                })
                .map(ConsumerRecord::value)
                .doOnError(e -> logger.error(e.getMessage()));
    }

    @Override
    public void run(String... args) throws Exception {

        process().subscribe();
    }
}
