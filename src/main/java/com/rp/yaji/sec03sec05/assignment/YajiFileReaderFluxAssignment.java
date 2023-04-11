package com.rp.yaji.sec03sec05.assignment;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.io.*;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class YajiFileReaderFluxAssignment {

    public static String BASE_DIRECTORY_PATH = "/home/yaji/Personal/git/github/udemy-java-reactive-programming-course";
    public static String RELATIVE_PATH = "/src/main/resources/assignment/sec0305/sec0305.txt";
    public static String FILE_PATH = BASE_DIRECTORY_PATH+RELATIVE_PATH;
    public static String WRONG_FILE_PATH = "/wrongFilePath.txt";
    public static void main(String[] args) {
        YajiFileReaderFluxAssignment assignment = new YajiFileReaderFluxAssignment();
        File file = new File(FILE_PATH);

        /*
        Looks like if stateSupplier() throws an Exception, a Flux.error() is generated.
        Because, on supplying "WONG_FILE_PATH", i.e. a non-existent path,
            it should've errored out in the stateSupplier() code,
            and what I received in the subscriber is "Got ERROR: /wrongFilePath.txt (No such file or directory)"

        Note that the same cannot be said if stateConsumer() throws an Exception.
        This is because the stream would've already been closed by then.
         */
        //File file = new File(WRONG_FILE_PATH);
        Flux<String> publisher = assignment.readFromFile(file);
        YajiSubscriber5 sub = new YajiSubscriber5();
        publisher
                /*
                Trying to test if BufferedReader will close on sudden emitting of error.
                Yes, I got the log "Closing BufferedReader.."
                 */
                .map(str -> {
                    int rnd = new Random().nextInt(10);
                    System.out.println("RandomInt: "+rnd);
                    if(rnd > 8){
                        // Flux.error(exc) is generated if we just throw the below exception "exc".
                        throw new RuntimeException("Throwing exception deliberately.");
                    }
                    return str;
                })
                .subscribeWith(sub);
    }

    private Callable<BufferedReader> stateSupplier(File file){
        return () -> new BufferedReader(new FileReader(file));
    }

    private BiFunction<BufferedReader, SynchronousSink<String>, BufferedReader> generator(){
        return ((bufferedReader, stringSynchronousSink) -> {
           try{
               String line = bufferedReader.readLine();
               if(line != null){
                   System.out.println("Emitting: "+line);
                   stringSynchronousSink.next(line);
               } else {
                   System.out.println("Emitting COMPLETE");
                   stringSynchronousSink.complete();
               }
           } catch(IOException e){
               System.out.println("IOException while reading line. "+e.getMessage());
               e.printStackTrace();
               System.out.println("Emitting ERROR");
               stringSynchronousSink.error(e);
           }
           return bufferedReader;
        });
    }

    private Consumer<BufferedReader> stateConsumer(){
        return bufferedReader -> {
            try{
                System.out.println("Closing BufferedReader..");
                bufferedReader.close();
            } catch(IOException e){
                System.out.println("Exception while closing BufferedReader: "+e.getMessage());
                e.printStackTrace();
            }
        };
    }

    public Flux<String> readFromFile(File file){
        return Flux.generate(
                stateSupplier(file),
                generator(),
                stateConsumer()
        );
    }

    public static class YajiSubscriber5<T> implements Subscriber<T> {

        private Subscription subscription;
        private int counter;
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T t) {
            System.out.println("Received: "+t);
            counter++;
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Got ERROR: "+throwable.getMessage());
            throwable.printStackTrace();
            System.out.println("Final Count: "+counter);
        }

        @Override
        public void onComplete() {
            System.out.println("Got COMPLETE");
            System.out.println("Final Count: "+counter);
        }
    }

}
