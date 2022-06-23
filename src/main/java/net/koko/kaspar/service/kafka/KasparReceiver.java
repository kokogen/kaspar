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

import java.util.List;

@Component
public class KasparReceiver implements CommandLineRunner {
    public static Logger logger = LoggerFactory.getLogger(KasparReceiver.class);
    @Autowired
    DataStorage storage;
    @Autowired
    StateStorage stateStorage;
    @Autowired
    ReceiverOptions<String, KasparItem> receiverOptions;
    @Value(value = "${topic}")
    String topic;

    @Override
    public void run(String... args) throws Exception {
        List<KasparTopicPartitionOffset> lstPartitions = stateStorage.readOffset(topic).collectList().block();

        receiverOptions.addAssignListener(
                receiverPartitions ->
                        receiverPartitions.forEach(p -> {
                            long offset = lstPartitions.stream()
                                    .filter(k -> k.getTopicPartition().getTopic().equals(topic) && (k.getTopicPartition().getPartition() == p.topicPartition().partition()))
                                    .findFirst()
                                    .get()
                                    .getOffset();
                            p.seek(offset);

                            logger.info(" %%%%%% {}, {}, {}", topic, p.topicPartition().partition(), offset);
                        })
        );

        KafkaReceiver.create(receiverOptions).doOnConsumer(consumer -> {
            lstPartitions.forEach(x -> {
                TopicPartition tp = new TopicPartition(topic, x.getTopicPartition().getPartition());
                consumer.seek(tp, x.getOffset());
            });
            return Mono.empty();
        });

        KafkaReceiver.create(receiverOptions).receive()
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
