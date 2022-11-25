package ru.practicum.ewmservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewmservice.model.statistic.EndpointHitDto;
import ru.practicum.ewmservice.model.statistic.ViewStats;

import java.util.List;
import java.util.Map;

@Service
public class PublicClient extends BaseClient {

    @Autowired
    public PublicClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addStats(EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public List<ViewStats> findStats(String start, String end, String uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris.toString(),
                "unique", unique
        );
        ResponseEntity<Object> entity = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        List<ViewStats> viewStatsList = (List<ViewStats>) entity.getBody();
        return viewStatsList;
    }

}
