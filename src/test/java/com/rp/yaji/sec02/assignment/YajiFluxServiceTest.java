package com.rp.yaji.sec02.assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class YajiFluxServiceTest {

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
  public void testCalculateNewPrice1(){
    YajiFluxService fluxService = new YajiFluxService();
    double actual = fluxService.calculateNewStockPrice1(100, 10, true);
    double expected = 110;
    Assertions.assertEquals(expected, actual);

    actual = fluxService.calculateNewStockPrice1(100, 10, false);
    expected = 90;
    Assertions.assertEquals(expected, actual);
  }
}
