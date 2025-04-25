package test.chatting.kafka.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.chatting.kafka.dto.ChatKafkaMessage;
import test.chatting.kafka.producer.KafkaChatProducer;

import java.time.Instant;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaChatProducer kafkaChatProducer;

    @PostMapping("/send")
    public String sendTestMessage(@RequestBody ChatKafkaMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now());
        }
        kafkaChatProducer.sendMessage(message);
        return "Kafka 메시지 전송 완료!";
    }
}
