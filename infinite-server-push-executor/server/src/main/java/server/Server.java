package server;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/write")
    @ResponseStatus(value = HttpStatus.OK)
    public void write(@RequestParam(value="payload") String payload) {
        LOGGER.info("Received from client: " + payload);
        final String clientIp = payload.split("%7", 2)[0];
        executor.execute(() -> this.pushToClient(clientIp));
        return;
    }

    public void pushToClient(final String clientIp) {
        String messageUri = UriComponentsBuilder.fromHttpUrl("http://" + clientIp + ":8080/client/push")
                                                .queryParam("payload", "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
                                                .build().encode().toUriString();

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