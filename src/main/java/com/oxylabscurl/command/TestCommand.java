package com.oxylabscurl.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@Slf4j
@Component
@CommandLine.Command(name = "test", description = "curl 封装 支持多次 curl 测试延迟.", mixinStandardHelpOptions = true)
public class TestCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return 10;
    }
}
