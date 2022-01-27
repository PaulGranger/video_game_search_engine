package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameInfoListener {

    protected final RestHighLevelClient restHighLevelClient;

    public GameInfoListener(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @RabbitListener
    public void onMessage(String message, @Header("game_id") String gameId) throws IOException {
        IndexRequest request = new IndexRequest("games");
        request.id(gameId);
        request.source(message, XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }
}
