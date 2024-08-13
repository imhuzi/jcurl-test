package com.oxylabscurl;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;

@SpringBootTest
class OxylabsCurlApplicationTests {

    @Test
    void contextLoads() {
        // 创建命令行对象
        org.apache.commons.exec.CommandLine cmdLine = org.apache.commons.exec.CommandLine.parse("echo 你好，Commons Exec");

        // 创建用于捕获输出的流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        // 设置PumpStreamHandler来捕获输出
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);

        try {
            // 执行命令
            executor.execute(cmdLine);

            // 打印输出和错误信息
            System.out.println("输出内容: " + outputStream.toString());
            System.out.println("错误内容: " + errorStream.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
