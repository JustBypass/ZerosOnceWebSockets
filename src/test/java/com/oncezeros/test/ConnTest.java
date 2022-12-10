package com.oncezeros.test;

import com.zw.game.ZerosOnce;
import com.zw.game.figures.Greeting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(classes = {ZerosOnce.class})
public class ConnTest {

    @Autowired
    private RSocketRequester requester;


    @Test
    public void  test1(){
        Flux<Greeting> result = requester
                .route("greeting.nikita")
                //.data(new Greeting("TEST"))
                .retrieveFlux(Greeting.class);


        System.out.println(result);
    }

    @Test
    public void  test2(){

    }
}
