package client;

import org.springframework.web.client.RestTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

import java.util.logging.Logger;

public class Client {
    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());

    private final static String serverUrl = "http://server:8080/server/greeting";

    public static void greet(RestTemplate restTemplate, String name) {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                                      .queryParam("name", name)
                                      .build().encode().toUriString();

        String message = restTemplate.getForObject(uri, String.class);
        LOGGER.info("Server message: " + message);
    }
}