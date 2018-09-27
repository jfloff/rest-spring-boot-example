package server;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicLong;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



@RestController
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * Integer.parseInt(System.getenv("THREAD_FACTOR") == null ? "1" : System.getenv("THREAD_FACTOR"));
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static final String clientUrl = "http://" + System.getenv("SERVER_OF") + ":8080/client/push";
    private static final String messageUri = UriComponentsBuilder.fromHttpUrl(clientUrl)
                                                          .queryParam("payload", "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
                                                          .build().encode().toUriString();

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/write")
    public String write(@RequestParam(value="payload") String payload) {
        LOGGER.info("Received from client: " + payload);
        executor.execute(() -> this.pushToClient());
        return payload;
    }

    public void pushToClient() {
        // this should be either replaced by a health-checking or
        // even better a eureka/zookeeper service discovery system
        while(true){
            try {
                LOGGER.info("Pushing to client ...");
                this.restTemplate.getForObject(messageUri, Void.class);
                LOGGER.info("Pushed!");
                return;
            } catch (RestClientException rce) {
                LOGGER.info("EXCEPTION on PUSH: " + rce.getMessage());
            }
        }
    }
}