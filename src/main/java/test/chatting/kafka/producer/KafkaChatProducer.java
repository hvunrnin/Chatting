package test.chatting.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import test.chatting.kafka.dto.ChatKafkaMessage;

@Component
@RequiredArgsConstructor
public class KafkaChatProducer {

    private final KafkaTemplate<String, ChatKafkaMessage> kafkaTemplate;
    private static final String TOPIC_NAME = "chat-message";

    public void sendMessage(ChatKafkaMessage message) {
        kafkaTemplate.send(TOPIC_NAME, message);
    }
}
