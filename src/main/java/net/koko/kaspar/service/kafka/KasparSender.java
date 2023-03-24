package net.koko.kaspar.service.kafka;

import net.koko.kaspar.model.data.KasparItem;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.List;

@Component
public class KasparSender {
    public static Logger logger = LoggerFactory.getLogger(KasparSender.class);

    @Value("${topic}")
    String topic;

    @Autowired
    KafkaSender<String, KasparItem> sender;

    public void send(List<KasparItem> items){
        Flux<SenderRecord<String, KasparItem, String>> records =
               Flux
                       .fromStream(items.stream())
                       .map(item ->SenderRecord.create(new ProducerRecord<>(topic, item.getKey(), item), ""));

        sender.send(records)
               .doOnError(e -> logger.error(e.getMessage()))
               .subscribe();
    }

}
