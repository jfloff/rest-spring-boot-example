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

    private static final int CLIENT_THREADS = Integer.parseInt(System.getenv("CLIENT_THREADS") == null ? "1" : System.getenv("CLIENT_THREADS"));

    private static final String serverUrl = "http://" + System.getenv("CLIENT_OF") + ":8080/server/write";
    private static final String messageUri = UriComponentsBuilder.fromHttpUrl(serverUrl)
                                                          .queryParam("payload", System.getenv("MY_IP") + "|CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC")
                                                          .build().encode().toUriString();
    @Autowired
    private RestTemplate restTemplate;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        for (int i = 0; i <= CLIENT_THREADS; i++) {
            new Thread(() -> {
                while(true) {
                    write();
                }
            }).start();
        }
    }

    @RequestMapping("/push")
    @ResponseStatus(value = HttpStatus.OK)
    public void push(@RequestParam(value="payload") String payload) {
        LOGGER.info("Received PUSH from Server: " + payload);
        return;
    }

    public void write() {
        // server might not be ready so we just log the exception and continue
        // this should be either replaced by a health-checking or
        // even better a eureka/zookeeper service discovery system
        try {
            restTemplate.getForObject(messageUri, Void.class);
            LOGGER.info("Server ACK!");
            return;
        } catch (RestClientException rce) {
            LOGGER.info("EXCEPTION on WRITE: " + rce.getMessage());
        }
    }
}