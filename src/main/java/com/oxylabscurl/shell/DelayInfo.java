package com.oxylabscurl.shell;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@Data
public class TimeInfo implements Serializable {
    private double namelookup = 0.00;
    private double connect = 0.00;
    private double appconnect = 0.00;
    private double redirect = 0.00;
    private double pretransfer = 0.00;
    private double starttransfer = 0.00;
    private double total = 0.00;
}
