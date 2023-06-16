package com.rp.yaji.sec02sec04.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;


public class YajiFluxServiceTest {

  @Test
  public void myCoreJavaTests(){
    Queue<Integer> q = new LinkedList<>();
    q.add(null);
    q.add(null);
    System.out.println(q);
  }

  @Test
  public void sampleTest(){
    Assertions.assertEquals(1, 1);
  }

  @Test
  public void testCalculateNewPrice1(){
    YajiFluxService fluxService = new YajiFluxService();
    double actual = fluxService.calculateNewStockPrice1(100, 10, true);
    double expected = 110;
    Assertions.assertEquals(expected, actual);

    actual = fluxService.calculateNewStockPrice1(100, 10, false);
    expected = 90;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testCalculateNewPrice2(){
    YajiFluxService fluxService = new YajiFluxService();
    double actual = fluxService.calculateNewStockPrice1(100, 10, true);
    double expected = 110;
    Assertions.assertEquals(expected, actual);

    actual = fluxService.calculateNewStockPrice1(100, 10, false);
    expected = 90;
    Assertions.assertEquals(expected, actual);
  }
}
