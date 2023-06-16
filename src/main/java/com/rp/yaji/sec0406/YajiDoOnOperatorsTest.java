package com.rp.yaji.sec0406;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class YajiDoOnOperatorsTest {

    /*
    Note the order of execution of each do-operator.
    For example,
        doFirst 2 (which is nearer to the subscriber) is called first because
            the pipeline is activated when the subscriber sends a subscribe signal.
            This flows up in the path from subscriber to publisher, and in the
            path starting from subscriber and ending at publisher, the doFirst-2 comes first
            and then doOnFirst-1.
        doOnSubscribe 1 (which is nearer to the publisher) is called first because
            the subscription object is sent in the path from publisher to subscriber
            and in this path doOnSubscribe-1 comes first and then doOnSubscribe-2
        doOnRequest 2 (which is nearer to the subscriber) is called first because
            the request(n) signal is sent from subscriber to the publisher, and in this path
            doOnRequest-2 appears before doOnRequest-1
        ... and so on...
     */
    public static class YDOOTSubscriber<T> implements Subscriber<T>{
        private Subscription subscription;
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T t) {
            System.out.println("onNext: "+t);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError: "+throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete: RECEIVED");
        }
    }

    public static void main (String[] args){
        YajiDoOnOperatorsTest test = new YajiDoOnOperatorsTest();
        YDOOTSubscriber<Integer> subscriber = new YDOOTSubscriber<Integer>();
        test.publishIntegers()
                .log()
                .doOnCancel(() -> System.out.println("doOnCancel 1"))
                .doFinally(signalType -> System.out.println("doFinally 1: SignalType :"+signalType))
                .doFirst(() -> System.out.println("doFirst 1"))
                .doAfterTerminate(() -> System.out.println("doAfterTerminate 1"))
                .doOnComplete(() -> System.out.println("doOnComplete 1"))
                .doOnDiscard(Integer.class, integer -> System.out.println("doOnDiscard 1: discarding "+integer))
                .doOnEach(signal -> System.out.println("doOnEach 1: signal received="+signal))
                .doOnError(e -> System.out.println("doOnError 1: error received="+e.getMessage()))
                .doOnNext(integer -> System.out.println("doOnNext 1: value received: "+integer))
                .doOnRequest(request -> System.out.println("doOnRequest 1: received request="+request))
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe 1: received subscription="+subscription))
                .doOnTerminate(() -> System.out.println("doOnTerminate 1"))
                .log()
                .doOnCancel(() -> System.out.println("doOnCancel 2"))
                .doFinally(signalType -> System.out.println("doFinally 2: SignalType :"+signalType))
                .doFirst(() -> System.out.println("doFirst 2"))
                .doAfterTerminate(() -> System.out.println("doAfterTerminate 2"))
                .doOnComplete(() -> System.out.println("doOnComplete 2"))
                .doOnDiscard(Integer.class, integer -> System.out.println("doOnDiscard 2: discarding "+integer))
                .doOnEach(signal -> System.out.println("doOnEach 2: signal received="+signal))
                .doOnError(e -> System.out.println("doOnError 2: error received="+e.getMessage()))
                .doOnNext(integer -> System.out.println("doOnNext 2: value received: "+integer))
                .doOnRequest(request -> System.out.println("doOnRequest 2: received request="+request))
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe 2: received subscription="+subscription))
                .doOnTerminate(() -> System.out.println("doOnTerminate 2"))
                .log()
                .subscribeWith(subscriber);
    }

    public Flux<Integer> publishIntegers(){
        return Flux.range(0,10);
    }
}
