package net.koko.kaspar.service.kafka;

import net.koko.kaspar.model.state.KasparTopicPartition;
import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.model.data.KasparItem;
import net.koko.kaspar.service.storage.DataStorage;
import net.koko.kaspar.service.storage.StateStorage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

@Service
@Profile("dev")
public class KasparReceiver implements CommandLineRunner {
    public static Logger logger = LoggerFactory.getLogger(KasparReceiver.class);
    @Autowired
    KafkaReceiver<String, KasparItem> receiver;

    @Autowired
    DataStorage storage;

    @Autowired
    StateStorage stateStorage;

    Flux<KasparItem> process(){

        return receiver.receive()
                .doOnNext(record -> {
                    KasparTopicPartitionOffset kasparTopicPartitionOffset = new KasparTopicPartitionOffset(new KasparTopicPartition(record.topic(), record.partition()), record.offset());
                    storage.save(kasparTopicPartitionOffset, record.key(), record.value());
                    stateStorage.saveTopicPartitionOffset(kasparTopicPartitionOffset).blockOptional();
                    //record.receiverOffset().acknowledge();
                    record.receiverOffset().commit().block();
                })
                .map(ConsumerRecord::value)
                .doOnError(e -> logger.error(e.getMessage()));
    }

    @Override
    public void run(String... args) throws Exception {
        process().subscribe();
    }
}
