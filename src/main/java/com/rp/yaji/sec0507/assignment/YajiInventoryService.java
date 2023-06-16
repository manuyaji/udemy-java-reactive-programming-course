package com.rp.yaji.sec0507.assignment;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;

public class YajiInventoryService implements Subscriber<YajiOrder> {

    private Subscription subscription;
    private Map<String, Integer> inventoryEmptied = new HashMap<>();

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(YajiOrder yajiOrder) {
        System.out.println("INVENTORY: onNext: "+yajiOrder);
        inventoryEmptied.put(yajiOrder.getProductKey(),
                inventoryEmptied.getOrDefault(yajiOrder.getProductKey(), 0)
                        + yajiOrder.getQuantity());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("INVENTORY: onError: "+throwable.getMessage());
        System.out.println("INVENTORY: "+inventoryEmptied);
    }

    @Override
    public void onComplete() {
        System.out.println("INVENTORY: onComplete. ");
        System.out.println("INVENTORY: "+inventoryEmptied);
    }
}
