package com.rp.yaji.sec02sec04.assignment;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class YajiFluxService2 {

    /*
  1. An emitter of random values within a percentage range of current value every second
  2. Custom?? Subscription which cancels after the necessary condition (10% above or below the fixed value)
  3. Subscriber which subscribes to above emitter with/without the custom subscription
   */


    public static void main(String[] args) throws Exception{
        double initialSharePrice = 457.66;
        YajiSubscriber2 ySubscriber = new YajiSubscriber2(initialSharePrice);
        YajiFluxService2 yService2 = new YajiFluxService2();
        yService2.emit(initialSharePrice).subscribeWith(ySubscriber);
        Thread.sleep(60*1000);
        System.out.println("Exiting main!");
    }

    public Flux<Double> emit(double initialSharePrice){
        Random random = new Random();
        int highestPercentageChange = 5;
        return Flux.interval(Duration.ofSeconds(2)).delayElements(Duration.ofSeconds(3))
                .map(sec -> {
                    System.out.println("===================");
                    System.out.println("Interval: "+sec);
                    int percentageChange = random.nextInt(highestPercentageChange);
                    boolean isNegative = random.nextBoolean();
                    if(isNegative){
                        return -1.0*percentageChange;
                    } else {
                        return 1.0*percentageChange;
                    }
                });
    }


    public static class YajiSubscriber2 implements Subscriber {

        private Subscription subscription;
        private AtomicReference<Double> currentSharePriceRef;
        private Double initialSharePrice;

        public YajiSubscriber2(Double initialSharePrice){
            currentSharePriceRef = new AtomicReference<>();
            currentSharePriceRef.set(initialSharePrice);
            this.initialSharePrice = initialSharePrice;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Object o) {
            System.out.println("onNext : The object got is "+o);
            Double percentageChange = (Double)o;
            Double oldSharePrice = currentSharePriceRef.getAndAccumulate(percentageChange,
                    (price, pchange) -> {
                        System.out.println("PercentChange: "+pchange);
                        System.out.println("price: "+price);
                        return price + pchange*price/100.0;
                    });
            System.out.println("New Share Price: "+currentSharePriceRef.get());
            if(shouldCancel(currentSharePriceRef.get())){
                System.out.println("Going to cancel subscription");
                this.subscription.cancel();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("onError: The error got is: "+throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("onComplete: The stream is COMPLETE!");
        }

        private boolean shouldCancel(double curSharePrice){
            double percentChange = (curSharePrice - initialSharePrice)*100.0/initialSharePrice;
            System.out.println("Percentage Change from initialSharePrice ["+initialSharePrice+"] : "+percentChange+"%");
            return (percentChange >= 10.0 || percentChange <= -10.0);
        }
    }

}
