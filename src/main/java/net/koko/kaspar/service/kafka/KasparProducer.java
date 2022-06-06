package net.koko.kaspar.service.kafka;

import net.koko.kaspar.model.data.KasparItem;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

import java.util.List;

@Component
public class KasparProducer {
    public static Logger logger = LoggerFactory.getLogger(KasparProducer.class);

    @Value("${topic}")
    String topic;

    @Autowired
    ReactiveKafkaProducerTemplate<String, KasparItem> producer;

    public void send(List<KasparItem> items){
       Flux<SenderRecord<String, KasparItem, String>> records =
               Flux
                       .fromStream(items.stream())
                       .map(item ->SenderRecord.create(new ProducerRecord<>(topic, item.getKey(), item), ""));

       producer.send(records)
               .doOnError(e -> logger.error(e.getMessage()))
               .subscribe();
    }

}
