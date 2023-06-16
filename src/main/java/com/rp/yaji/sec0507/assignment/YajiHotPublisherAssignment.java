package com.rp.yaji.sec0507.assignment;

import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

public class YajiHotPublisherAssignment {

    public static void main(String[] args) throws Exception{
        YajiOrderService orderService = YajiOrderService.getInstance();
        Subscriber<YajiOrder> revenueSub = new YajiRevenueService();
        Subscriber<YajiOrder> inventorySub = new YajiInventoryService();
        Flux<YajiOrder> hotPublisher = orderService.hotPublishOrders();
        hotPublisher.subscribeWith(revenueSub);
        hotPublisher.subscribeWith(inventorySub);
        Thread.sleep(30000);
    }

}
