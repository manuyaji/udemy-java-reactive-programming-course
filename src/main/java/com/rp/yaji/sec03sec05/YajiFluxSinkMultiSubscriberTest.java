package com.rp.yaji.sec03sec05;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.Function;

public class YajiFluxSinkMultiSubscriberTest {

    private static BiFunction<Integer, Integer, Boolean> FUNC_LIMIT_1000 = (state, newVal) -> newVal>1000;

    private static BiFunction<Integer, Integer, Boolean> FUNC_LIMIT_900 = (state, newVal) -> newVal>900;

    private CountDownLatch countDownLatch = new CountDownLatch(2);
    public static void main(String[] args) throws Exception{
        YajiFluxSinkMultiSubscriberTest test = new YajiFluxSinkMultiSubscriberTest();
        int rangeEnd = 1100, maxNumOfElemsToBeProduced = 30000;
        //Flux<Integer> publisher = test.produceRandomIntegers(rangeEnd);
        Flux<Integer> publisher = test.produceRandomIntegersUsingFluxSink(rangeEnd, maxNumOfElemsToBeProduced);
        YajiSubscriber3 sub1 = new YajiSubscriber3("sub1", test.countDownLatch, Function.identity(), FUNC_LIMIT_900);
        YajiSubscriber3 sub2 = new YajiSubscriber3("sub2", test.countDownLatch, Function.identity(), FUNC_LIMIT_1000);
        publisher.subscribeWith(sub1);
        publisher.subscribeWith(sub2);
        test.countDownLatch.await();
    }

    public Flux<Integer> produceRandomIntegersUsingFluxSink(int rangeEnd, int maxNumOfElemsToBeProduced){
        return Flux.create(fluxSink -> {
            Random random = new Random();
            int count = 0;
            while(count < maxNumOfElemsToBeProduced && !fluxSink.isCancelled()) {
                int val = random.nextInt(rangeEnd);
                System.out.println("PRODUCER: "+val);
                fluxSink.next(val);
                count++;
            }
            countDownLatch.countDown();
            fluxSink.complete();
            System.out.println("PRODUCER: Stopping production. Count: "+count+" ; maxElems: "+maxNumOfElemsToBeProduced+" ; isCancelled: "+fluxSink.isCancelled());
        });
    }

    public Flux<Integer> produceRandomIntegers(int rangeEnd){
        Random random = new Random();
        return Flux.interval(Duration.ofSeconds(1)).delayElements(Duration.ofSeconds(2))
                .log().map(interval -> {
                    int val = random.nextInt(rangeEnd);
                    System.out.println("PRODUCER: "+val);
                    return val;
                }).log();
    }

    public static class YajiSubscriber3<T> implements Subscriber<T> {

        public YajiSubscriber3(String name, CountDownLatch countDownLatch, Function<T, T> stateCalculator, BiFunction<T, T, Boolean> terminationChecker){
            this.name = name;
            this.stateCalculatingFunction = stateCalculator;
            this.terminationChecker = terminationChecker;
            this.countDownLatch = countDownLatch;
        }

        CountDownLatch countDownLatch;
        Function<T, T> stateCalculatingFunction;
        BiFunction<T, T, Boolean> terminationChecker;
        T state;
        private Subscription subscription;
        private String name;
        @Override
        public void onSubscribe(Subscription subscription) {
            log("Received subscription: "+subscription);
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        private void log(String message){
            System.out.println("Sub "+name+" : "+message);
        }

        @Override
        public void onNext(T t) {
            log("Received "+t);
            log("Applying TerminationChecker..");
            if(terminationChecker.apply(state, t)){
                log("Cancelling subscription...");
                countDownLatch.countDown();
                this.subscription.cancel();
            } else {
                log("Not terminating.. Continuing..");
            }
            log("Applying State Calculation Function..");
            state = stateCalculatingFunction.apply(t);
            log("Applied stateCalculating function.. New value: "+state);
        }

        @Override
        public void onError(Throwable throwable) {
            log("Received error - "+throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            log("Received ONCOMPLETE");
        }
    }

}
