package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.nio.file.Paths;

@SpringBootApplication
public class Launcher {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
                ObjectMapper objectMapper = new ObjectMapper();
                DataGame[] dataGames = objectMapper.readValue(Paths.get(args[0]).toFile(), DataGame[].class);
                RabbitTemplate rabbitTemplate = springContext.getBean(RabbitTemplate.class);
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                for (DataGame dataGame : dataGames) {
                    rabbitTemplate.convertAndSend("", "game_info", dataGame, message -> {
                        message.getMessageProperties().getHeaders().put("game_id", dataGame.id);
                        return message;
                    });
                }
            }
        }
    }
}
