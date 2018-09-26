package server;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicLong;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.web.client.RestClientException;



@RestController
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final String clientUrl = "http://" + System.getenv("SERVER_OF") + ":8080/client/push";
    private final AtomicLong counter = new AtomicLong();
    @Autowired
    private String fixedLengthMessage;
    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/write")
    public String write(@RequestParam(value="payload") String payload) {
        try {
            LOGGER.info("Received from client: " + payload);
            return payload;
        } finally {
            Thread t = new Thread(() -> this.pushToClient());
            t.start();
        }
    }

    public void pushToClient() {
        String uri = UriComponentsBuilder.fromHttpUrl(clientUrl)
                                      .queryParam("payload", String.format("[%d] %s", counter.incrementAndGet(), this.fixedLengthMessage))
                                      .build().encode().toUriString();

        // this should be either replaced by a health-checking or
        // even better a eureka/zookeeper service discovery system
        while(true){
            try {
                LOGGER.info("Pushing to client ...");
                this.restTemplate.getForObject(uri, Void.class);
                LOGGER.info("Pushed!");
                return;
            } catch (RestClientException rce) {
                LOGGER.info("EXCEPTION on PUSH: " + rce.getMessage());
            }
        }
    }
}