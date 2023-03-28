package com.rp.yaji.sec02.assignment;

import java.time.Duration;
import java.util.Random;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;


public class YajiFluxService {

  /*
  1. An emitter of random values around a fixed value every second
  2. Custom?? Subscription which cancels after the necessary condition (10% above or below the fixed value)
  3. Subscriber which subscribes to above emitter with/without the custom subscription
   */

  public static void main(String[] args){
    double baseValue = 100;
    Subscriber<Double> subscriber = new YajiSubscriber(baseValue);
    YajiFluxService fluxService = new YajiFluxService();
    fluxService.publishStockPrices(baseValue).log().subscribe(subscriber);
    /*
    publishStockPrices(baseValue)
        .subscribe(
        val -> {
          System.out.println("onNext Received "+val);
          double percentageChange = percentageChange(baseValue, val);
          System.out.println("% change: "+percentageChange);
        },
        throwable -> {
          System.out.println("onError Called");
          throwable.printStackTrace();
        },
        () -> {
          System.out.println("onComplete called");
        }
        );
     */

    try{
      Thread.sleep(50*1000);
      System.out.println("Exiting...");
    } catch (InterruptedException e){
      e.printStackTrace();
    }
  }

  public Flux<Double> publishStockPrices(double baseValue){
    Random random = new Random();
    //return Flux.interval(Duration.ofSeconds(1)).delayElements(Duration.ofSeconds(2)).log().map(interval -> random.nextInt(11)).log().map(percent -> calculateNewStockPrice(baseValue, percent)).log();
    return Flux.interval(Duration.ofSeconds(1))
        .log()
        .onBackpressureDrop()
        .log()
        .delayElements(Duration.ofSeconds(2))
        .log()
        .map(interval -> new Object[]{random.nextInt(11), random.nextBoolean()})
        .log()
        .map(tuple -> calculateNewStockPrice1(baseValue, (int)tuple[0], (boolean)tuple[1]))
        .log();
  }

  public double calculateNewStockPrice1(double baseValue, int percent, boolean isPositive){
    return isPositive ? baseValue + baseValue*percent/100 : baseValue - baseValue*percent/100;
  }

  public double calculateNewStockPriceOld(double baseValue, double rnd){
    if(rnd == 0.5){
      return baseValue - 0.01 * baseValue;
    } else if(rnd < 0.5){
      return baseValue - (rnd*baseValue/5) - 0.02 * baseValue;
    } else {
      return baseValue + (baseValue*(rnd-0.5)/5) + 0.02 * baseValue;
    }
  }

  public double calculateNewStockPrice(double baseValue, int percent){
    boolean isPositive = new Random().nextBoolean();
    return isPositive ? baseValue + percent*baseValue/100 : baseValue - percent*baseValue/100;
  }

  public static class YajiSubscriber implements Subscriber<Double> {

    private Subscription subscription;
    private final double baseValue;

    public YajiSubscriber (double baseValue){
      this.baseValue = baseValue;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
      this.subscription = subscription;
      System.out.println("Subscribed to this subscription ["+subscription+"]. BaseValue: "+baseValue);
      subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Double dbl) {
      System.out.println("onNext Received "+dbl);
      double percentageChange = percentageChange(baseValue, dbl);
      System.out.println("% change: "+percentageChange);
      if(percentageChange >= 10.0 || percentageChange <= -10.0){
        System.out.println("Cancelling subscription.");
        subscription.cancel();
      }
    }

    @Override
    public void onError(Throwable throwable) {
      System.out.println("onError Called");
      throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
      System.out.println("onComplete called");
    }

    public static double percentageChange(double baseValue, double newValue){
      return (newValue - baseValue)*100/baseValue;
    }
  }

}
