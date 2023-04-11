package com.rp.yaji.playground;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class YajiTestOperatorsReturnNewInstance {

    /*
    In Section 6 (Operators), lesson 64 (Introduction), it was said
    1. Operators are Decorators
    2. On applying operator, a new instance is returned.
    This is for testing the second sentence - "On applying operator, a new instance is returned"

    INFERENCE: Looks like the statement 2 is INCORRECT.
    Got this output:
        """
        Generating: YajiState{hashcode=1123225098, counter=0, value=185}
        OBJECT: YajiState{hashcode=1123225098, counter=1, value=185}
        OBJECT: YajiState{hashcode=1123225098, counter=2, value=185}
        OBJECT: YajiState{hashcode=1123225098, counter=3, value=185}
        RECEIVED: YajiState{hashcode=1123225098, counter=3, value=185}
        """
    Look at the hashcode -> it is the same across multiple operators.
     */

    public static class YajiState<T>{
        public YajiState(T val){
            this.value = val;
        }
        int counter;
        T value;
        public void increment(){
            counter++;
        }

        public int getCounter() {
            return counter;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "YajiState{" +
                    "hashcode=" + hashCode() +
                    ", counter=" + counter +
                    ", value=" + value +
                    '}';
        }
    }

    public static void testYajiStateIfHashcodeDiffersIfValueAndCounterIsTheSameValue(){
        YajiState<Integer> y1 = new YajiState<>(185);
        YajiState<Integer> y2 = new YajiState<>(185);
        System.out.println(y1.equals(y2));
        System.out.println("y1: "+y1);
        System.out.println("y2: "+y2);
        /*
        Output:
        """
        false
        y1: YajiState{hashcode=980546781, counter=0, value=185}
        y2: YajiState{hashcode=321001045, counter=0, value=185}
        """
         */
    }

    private Callable<Integer> countOfElementsAsStateSupplier(){
        return () -> 0;
    }

    private BiFunction<Integer, SynchronousSink<YajiState<Integer>>, Integer> generator(Random random, int rangeEnd){
        return (countOfElements, yajiStateSynchronousSink) -> {
            YajiState<Integer> yajiState = new YajiState<>(random.nextInt(rangeEnd));
            System.out.println("Generating: "+yajiState);
            yajiStateSynchronousSink.next(yajiState);
            return countOfElements++;
        };
    }

    private Consumer<Integer> stateConsumer(){
        return countOfElements -> {
            System.out.println("Final Count of Elements: "+countOfElements);
        };
    }
    public Flux<YajiState<Integer>> publishIntegers(){
        Random random = new Random(500);
        int rangeEnd = 500;
        return Flux.generate(
                countOfElementsAsStateSupplier(),
                generator(random, rangeEnd),
                stateConsumer()
        );
    }

    public static void main(String[] args) {
        testYajiStateIfHashcodeDiffersIfValueAndCounterIsTheSameValue();
        /*
        YajiTestOperatorsReturnNewInstance test = new YajiTestOperatorsReturnNewInstance();
        Flux<YajiState<Integer>> originalPublisher = test.publishIntegers();
        Flux<YajiState<Integer>> finalPublisher = originalPublisher
                .map(ys -> {
                    ys.increment();
                    System.out.println("OBJECT: "+ys);
                    return ys;
                })
                .map(ys -> {
                    ys.increment();
                    System.out.println("OBJECT: "+ys);
                    return ys;
                })
                .map(ys -> {
                    ys.increment();
                    System.out.println("OBJECT: "+ys);
                    return ys;
                });
        finalPublisher
                .take(20)
                .subscribe(
                        val -> System.out.println("RECEIVED: "+val),
                        error -> {
                            System.out.println("Received ERROR: "+error.getMessage());
                            error.printStackTrace();
                            },
                        () -> System.out.println("Received COMPLETE")
                );
         */
    }
}
