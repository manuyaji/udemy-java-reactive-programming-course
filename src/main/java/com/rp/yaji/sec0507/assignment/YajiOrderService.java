package com.rp.yaji.sec0507.assignment;

import com.github.javafaker.Faker;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

public class YajiOrderService {

    private static final YajiOrderService yajiOrderService = new YajiOrderService();

    private YajiOrderService(){}

    public static YajiOrderService getInstance(){
        return yajiOrderService;
    }

    public static final Integer MAX_PRODUCTS = 10;
    private Faker faker = new Faker();
    private Flux<YajiOrder> hotFlux;

    private YajiOrder createNewRandomOrder(){
        YajiOrder order = new YajiOrder();
        String productName = faker.commerce().department();
        order.setProductName(productName);
        order.setQuantity(new Random().nextInt(5)+1);
        order.setProductKey(productName);
        order.setUserId(new Random().nextInt(5)+1);
        order.setPricePerQuantity(Double.parseDouble(faker.commerce().price()));
        return order;
    }

    public Flux<YajiOrder> publishOrders(){
        return Flux.generate(
                () -> 0,
                (stateNumOfItemsProcessed, synchronousSink) -> {
                    synchronousSink.next(createNewRandomOrder());
                    stateNumOfItemsProcessed++;
                    if(stateNumOfItemsProcessed >= MAX_PRODUCTS){
                        synchronousSink.complete();
                    }
                    return stateNumOfItemsProcessed;
                },
                stateNumOfItemsProcessed -> System.out.println("Number of items processed: "+stateNumOfItemsProcessed)
        );
    }

    public Flux<YajiOrder> hotPublishOrders(){
        if(hotFlux == null){
            hotFlux = publishOrders().delayElements(Duration.ofSeconds(1)).cache();
        }
        return hotFlux;
    }
}
