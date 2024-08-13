package com.oxylabscurl.shell;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@Data
public class DelayInfo implements Serializable {
    @Excel(name = "seeeid(IP)")
    private String sessid="";
    @Excel(name = "第N次测试")
    private int count=0;
    @Excel(name = "curl地址",width = 60)
    private String url;
    @Excel(name = "namelookup",width = 30)
    private double namelookup = 0.00;
    @Excel(name = "connect")
    private double connect = 0.00;
    @Excel(name = "appconnect")
    private double appconnect = 0.00;
    @Excel(name = "redirect")
    private double redirect = 0.00;
    @Excel(name = "pretransfer")
    private double pretransfer = 0.00;
    @Excel(name = "starttransfer")
    private double starttransfer = 0.00;
    @Excel(name = "total")
    private double total = 0.00;


    @Excel(name = "curl command")
    private String curl;
}
