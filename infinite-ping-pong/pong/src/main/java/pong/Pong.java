package pong;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicLong;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.web.client.RestClientException;



@RestController
public class Pong {
    private static final Logger LOGGER = Logger.getLogger(Pong.class.getName());

    private static final String PONG = "PONG";
    private static final String pingUri = UriComponentsBuilder.fromHttpUrl("http://ping-srv:8080/ping/echo").build().encode().toUriString();
    private final AtomicLong counter = new AtomicLong();
    private final ReentrantLock lock = new ReentrantLock();
    @Autowired
    private String fixedLengthMessage;
    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/echo")
    public String echo() {
        new Thread(() -> this.ping()).start();
        return String.format("[%d] %s", counter.incrementAndGet(), this.fixedLengthMessage);
    }

    public void ping() {
        lock.lock();
        try {
            // this should be either replaced by a health-checking or
            // even better a eureka/zookeeper service discovery system
            while(true){
                try {
                    LOGGER.info(this.restTemplate.getForObject(pingUri, String.class));
                    return;
                } catch (RestClientException rce) {
                    LOGGER.info("EXCEPTION on PONG: " + rce.getMessage());
                } finally {
                    // always wait 2 seconds for next message or error
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException ie) { }
                }
            }
        } finally {
           lock.unlock();
        }
    }
}