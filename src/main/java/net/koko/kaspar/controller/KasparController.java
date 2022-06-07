package net.koko.kaspar.controller;

import net.koko.kaspar.model.data.KasparItem;
import net.koko.kaspar.model.state.KasparTopicPartitionOffset;
import net.koko.kaspar.service.kafka.KasparSender;
import net.koko.kaspar.service.storage.StateStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/kaspar")
@Profile("dev")
public class KasparController {
    public static Logger logger = LoggerFactory.getLogger(KasparController.class);

    @Autowired
    KasparSender sender;

    @Autowired
    StateStorage stateStorage;

    @PostMapping("/messages/send")
    public void send(@RequestBody List<KasparItem> items){
        sender.send(items);

    }

    @GetMapping("/state/{topic}")
    public Flux<KasparTopicPartitionOffset> readOffset(@PathVariable("topic") String topic){
        return stateStorage.readOffset(topic);
    }
}
