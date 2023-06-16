package com.rp.yaji.sec0507.assignment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class YajiOrder {

    private String productKey;
    private String productName;
    private Double pricePerQuantity;
    private int quantity;
    private long userId;

}
