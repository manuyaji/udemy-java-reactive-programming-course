package com.rp.yaji.sec01sec03.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import reactor.core.publisher.Mono;


public class YajiFileService {

  public Mono<String> readAndReturnContent(File file) {
    return Mono.fromSupplier(() -> {
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder fullContentBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
          fullContentBuilder = fullContentBuilder.append(line);
        }
        return fullContentBuilder.toString();
      } catch (FileNotFoundException e) {
        System.out.println("ERROR while reading file: FileNotFound");
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (IOException e) {
        System.out.println("ERROR while reading file: IOException");
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    });
  }

  public Mono<Void> createAndWriteContent(String str, File file, boolean appendIfExists) {
    return Mono.fromRunnable(() -> {
      try {
        if(file.exists() && appendIfExists){
          PrintWriter printWriter = new PrintWriter(file);
          printWriter.append(str).close();
          //return Void;
        } else if(!file.exists()) {
          file.createNewFile();
        }
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.write(str);
        printWriter.close();
        //return true;
      } catch (FileNotFoundException e) {
        System.out.println("ERROR while initiating file write: FileNotFound");
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (IOException e) {
        System.out.println("ERROR while creating file: IOException");
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    });
  }

  public Mono<Boolean> deleteFile(File file) {
    return Mono.fromSupplier(() -> {
      try {
        return file.delete();
      } catch (SecurityException e) {
        System.out.println("ERROR while deleting file: SecurityException");
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    });
  }

  public static void main(String[] args) {
    File testFile = new File("/home/yaji/Personal/git/github/udemy-java-reactive-programming-course/sec01-test-file.txt");
    YajiFileService yajiFileService = new YajiFileService();
    yajiFileService.createAndWriteContent("Om Ganapataye Namah!", testFile, false)
        .onErrorMap(error -> {
          error.printStackTrace();
          return error;
        })
        .onErrorStop()
        .then(Mono.fromSupplier(() ->   {
          System.out.println("Was the file created? Yes!!");
          return yajiFileService.readAndReturnContent(testFile);
        })).flatMap(readFileMono -> {
          return readFileMono.flatMap(contentOnFile -> {
            System.out.println("Content on File: [ " + contentOnFile + " ]");
            return yajiFileService.deleteFile(testFile);
          });
        }).subscribe(bool -> System.out.println("Was the file deleted? " + bool), err -> {
          System.out.println("There was an Error in the pipeline!!");
          err.printStackTrace();
        }, () -> System.out.println("All completed successfully!"));
    try{
      Thread.sleep(5000);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

}
