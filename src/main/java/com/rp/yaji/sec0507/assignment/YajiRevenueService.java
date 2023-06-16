package com.rp.yaji.sec0507.assignment;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;

public class YajiRevenueService implements Subscriber<YajiOrder> {

    private Subscription subscription;
    private Map<String, Double> revenueMap = new HashMap<>();

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(YajiOrder yajiOrder) {
        System.out.println("REVENUE: onNext: "+yajiOrder);
        revenueMap.put(yajiOrder.getProductKey(),
                revenueMap.getOrDefault(yajiOrder.getProductKey(), 0.0)
                        + yajiOrder.getPricePerQuantity() * yajiOrder.getQuantity());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("REVENUE: onError: "+throwable.getMessage());
        System.out.println("REVENUE: "+revenueMap);
    }

    @Override
    public void onComplete() {
        System.out.println("REVENUE: onComplete. ");
        System.out.println("REVENUE: "+revenueMap);
    }
}
