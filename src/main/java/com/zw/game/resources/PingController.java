package com.zw.game.resources;

import com.zw.game.figures.Greeting;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Controller
public class PingController {

    private final Logger log = LoggerFactory.getLogger(PingController.class);

    private final List<RSocketRequester> CLIENTS = new ArrayList<>();

    @MessageMapping("greeting.{name}")
    public Flux<Greeting> meet(@DestinationVariable String name){
        return Flux
                .fromStream(Stream.generate(
                        ()-> new Greeting("Hello"+name+" @ "+ Instant.now()+"!!!")
                ))
                .take(10)
                .delayElements(Duration.ofMillis(200));
    }

    @ConnectMapping("connect")
    public void doConnect(RSocketRequester requester, @Payload String client){
        requester.rsocket()
                .onClose() // (1)
                .doFirst(() -> {
                    log.info("Client: {} CONNECTED.", client);
                    CLIENTS.add(requester); // (2)
                })
                .doOnError(error -> {
                    log.warn("Channel to client {} CLOSED", client); // (3)
                })
                .doFinally(consumer -> {
                    CLIENTS.remove(requester);
                    log.info("Client {} DISCONNECTED", client); // (4)
                })
                .subscribe();
    }
}
