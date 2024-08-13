package com.oxylabscurl.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@Component
@Slf4j
public class ShellExec {
    /**
     * commond
     *
     * @param command
     * @return
     */
    public ShellExecResult exec(String command) {
        ShellExecResult result = new ShellExecResult();
        // 创建命令行对象
        CommandLine cmdLine = CommandLine.parse(command);
        // 创建用于捕获输出的流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        // 设置PumpStreamHandler来捕获输出
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        try {
            // 执行命令
            int code = executor.execute(cmdLine);
            result.setExitCode(code);
            // 打印输出和错误信息
            result.setStdout(outputStream.toString());
            result.setError(errorStream.toString());
            log.info("command result: {}", result);
        } catch (Exception e) {
            result.setError(e.getMessage());
        }
        return result;
    }
}
