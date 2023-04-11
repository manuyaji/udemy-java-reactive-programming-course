package com.rp.yaji.sec0406;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

public class YajiHandleOperatorTest {

    public Flux<String> publishCountryNames(){
        Faker faker = new Faker();
        return Flux.generate(
                () -> 0,
        (state, synchronousSink) -> {
            synchronousSink.next(faker.country().name());
            return ++state;
        },
                state -> System.out.println("Final Count: "+state));
    }

    public static void main(String[] args) {
        YajiHandleOperatorTest test = new YajiHandleOperatorTest();
        test.publishCountryNames().handle(
                (countryName, synchronousSink) ->{
                    synchronousSink.next(countryName);
                    if(countryName.toLowerCase().equals("canada")){
                        synchronousSink.complete();
                    }
                }
        ).subscribe(
                countryName -> System.out.println("RECEIVED: "+countryName),
                error -> {
                    System.out.println("ERROR received: "+error.getMessage());
                    error.printStackTrace();
                },
                () -> System.out.println("Received COMPLETE")
        );
    }
}
