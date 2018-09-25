package server;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@RestController
public class Server {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        LOGGER.info(name + ": Hello there!");

        String message = String.format("[%d] General Grievous: General %s! You are a bold one...", counter.incrementAndGet(), name);
        return message;
    }
}