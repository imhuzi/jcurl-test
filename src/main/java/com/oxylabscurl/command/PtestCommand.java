package com.oxylabscurl.command;

import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@CommandLine.Command(name = "ptest", description = "基于代理的 curl 封装 支持多次 curl 测试延迟.", mixinStandardHelpOptions = true)
public class PcurlCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        return 11;
    }
}
