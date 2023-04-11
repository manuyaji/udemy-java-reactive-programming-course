package com.rp.yaji.sec03sec05.assignment;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

public class YajiFluxGenerateSynchronousSinkTest {

    private Faker faker = new Faker();

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        YajiSubscriber4 sub = new YajiSubscriber4(latch);
        YajiFluxGenerateSynchronousSinkTest test = new YajiFluxGenerateSynchronousSinkTest();
        Flux<String> publisher = test.countryNamesPublisher();
        publisher.subscribeWith(sub);
    }

    public Flux<String> countryNamesPublisher(){
        return Flux.generate(stringSynchronousSink -> {
            String countryName = faker.country().name();
            stringSynchronousSink.next(countryName);
            if(countryName.toLowerCase().equals("canada")){
                stringSynchronousSink.complete();
            }
        });
    }

    public static class YajiSubscriber4<T> implements Subscriber<T> {

        public YajiSubscriber4(CountDownLatch latch){
            this.latch = latch;
        }
        private CountDownLatch latch;
        private int count;
        private Subscription subscription;
        private T state;
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T t) {
            System.out.println("Received: "+t);
            count++;
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Got ERROR: "+throwable.getMessage());
            throwable.printStackTrace();
            latch.countDown();
            System.out.println("Total count: "+count);
        }

        @Override
        public void onComplete() {
            System.out.println("Got COMPLETE");
            System.out.println("Total count: "+count);
            latch.countDown();
        }
    }
}
