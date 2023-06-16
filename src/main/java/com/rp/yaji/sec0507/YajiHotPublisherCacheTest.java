package com.rp.yaji.sec0507;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class YajiHotPublisherCacheTest {

    public static class YHPCTSubscriber<T> implements Subscriber<T>{

        private Subscription subscription;
        private String name;

        public YHPCTSubscriber(String name){
            this.name = name;
        }
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T t) {
            System.out.println(name+" onNext RECEIVED: "+t);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println(name+" onError RECEIVED: "+throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println(name+" onComplete RECEIVED.");
        }
    }

    public Flux<Integer> fluxOfIntegers(){
        return Flux.range(1,10);
    }

    public static void main(String[] args) {
        YHPCTSubscriber<Integer> sub1 = new YHPCTSubscriber<>("sub1");
        YHPCTSubscriber<Integer> sub2 = new YHPCTSubscriber<>("sub2");
        YajiHotPublisherCacheTest test = new YajiHotPublisherCacheTest();
        Flux<Integer> cachedPublisher = test.fluxOfIntegers()
                //.log()
                .delayElements(Duration.ofSeconds(1))
                //.log()
                .cache(5)
                ;
        /*
        try {
            Thread.sleep(4000);
        } catch(InterruptedException e){
            System.out.println("InterruptedException 1..");
            e.printStackTrace();
        }
        */
        cachedPublisher.subscribeWith(sub1);
        try {
            Thread.sleep(8000);
        } catch(InterruptedException e){
            System.out.println("InterruptedException 2..");
            e.printStackTrace();
        }
        cachedPublisher.subscribeWith(sub2);
        try {
            Thread.sleep(40000);
        } catch(InterruptedException e){
            System.out.println("InterruptedException 3..");
            e.printStackTrace();
        }
        System.out.println("Exiting...");
    }
}