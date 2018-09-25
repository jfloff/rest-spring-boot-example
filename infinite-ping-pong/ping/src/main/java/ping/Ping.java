package ping;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
public class Ping {
    private static final Logger LOGGER = Logger.getLogger(Ping.class.getName());

    private static final String pongUri = UriComponentsBuilder.fromHttpUrl("http://pong-srv:8080/pong/echo").build().encode().toUriString();
    private final AtomicLong counter = new AtomicLong();
    private final ReentrantLock lock = new ReentrantLock();
    @Autowired
    private String fixedLengthMessage;
    @Autowired
    private RestTemplate restTemplate;


    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        pong();
    }

    @RequestMapping("/echo")
    public String echo() {
        new Thread(() -> this.pong()).start();
        return String.format("[%d] %s", counter.incrementAndGet(), this.fixedLengthMessage);
    }

    public void pong() {
        lock.lock();
        try {
            // this should be either replaced by a health-checking or
            // even better a eureka/zookeeper service discovery system
            while(true){
                try {
                    LOGGER.info(this.restTemplate.getForObject(pongUri, String.class));
                    return;
                } catch (RestClientException rce) {
                    LOGGER.info("EXCEPTION on PING: " + rce.getMessage());
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