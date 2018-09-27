package client;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private static final String serverUrl = "http://" + System.getenv("CLIENT_OF") + ":8080/server/write";
    private final AtomicLong counter = new AtomicLong();
    @Autowired
    private String fixedLengthMessage;
    @Autowired
    private RestTemplate restTemplate;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        new Thread(() -> {
            while(true) {
                write();
            }
        }).start();
    }

    @RequestMapping("/push")
    @ResponseStatus(value = HttpStatus.OK)
    public void push(@RequestParam(value="payload") String payload) {
        LOGGER.info("Received PUSH from Server: " + payload);
        return;
    }

    public void write() {
        String uri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                                      .queryParam("payload", String.format("[%d] %s", counter.incrementAndGet(), this.fixedLengthMessage))
                                      .build().encode().toUriString();

        // server might not be ready so we just log the exception and continue
        // this should be either replaced by a health-checking or
        // even better a eureka/zookeeper service discovery system
        try {
            String reply = restTemplate.getForObject(uri, String.class);
            LOGGER.info("Server reply: " + reply);
            return;
        } catch (RestClientException rce) {
            LOGGER.info("EXCEPTION on WRITE: " + rce.getMessage());
        }
    }
}