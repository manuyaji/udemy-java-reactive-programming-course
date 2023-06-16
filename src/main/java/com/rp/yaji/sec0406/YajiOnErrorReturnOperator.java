package com.rp.yaji.sec0406;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class YajiOnErrorReturnOperator {

    public static void main(String[] args) {
        YajiOnErrorReturnOperator test = new YajiOnErrorReturnOperator();
        YOEROSubscriber<Integer> sub = new YOEROSubscriber<>();
        test.publishIntegers()
                .log()
                .map(i -> {
                    int z = 100/(50-i);
                    return i;
                })
                .log() // CANCEL SIGNAL here on encountering error in the next line (onErrorReturn)
                .onErrorReturn(-1)
                .log() // COMPLETE SIGNAL SENT on encountering error in previous line (onErrorReturn)
                .subscribeWith(sub);
    }

    public Flux<Integer> publishIntegers(){
        return Flux.range(0,100);
    }

    public static class YOEROSubscriber<T> implements Subscriber<T> {

        private Subscription subscription;
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T t) {
            System.out.println("onNext Received: "+t);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError Received: "+throwable.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("ONCOMPLETE Received. ");
        }
    }
}
