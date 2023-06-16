package com.rp.yaji.playground;

import java.util.function.Function;

public class LambdaPlayground {

    public static Function<Integer, String> numeralToStringConverter(){
        return i -> i.toString();
    }

    @FunctionalInterface
    public static interface TriFunction<A, B, C, R> {
        public R apply(A a, B b, C c);
    }

    public static TriFunction<Integer, Integer, Integer, Integer> functionWhichSumsThreeNumbers(){
        return (a,b,c) -> a+b+c;
    }

    public static void main(String[] args){
        System.out.println(functionWhichSumsThreeNumbers().apply(1,2,3));
    }

}
