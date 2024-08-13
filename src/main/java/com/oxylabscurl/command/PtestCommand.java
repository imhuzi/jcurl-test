package com.oxylabscurl.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxylabscurl.shell.DelayInfo;
import com.oxylabscurl.shell.ShellExec;
import com.oxylabscurl.shell.ShellExecResult;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@CommandLine.Command(name = "ptest", description = "基于代理的 curl 封装 支持多次 curl 测试延迟.", mixinStandardHelpOptions = true)
@Slf4j
@Component
public class PtestCommand implements Callable<Integer> {
    ObjectMapper mapper = new ObjectMapper();
    // user,cc,sessid,sesstime,pass, url
    private String commond = "curl -w \"@curl-format.txt\" -x pr.oxylabs.io:7777 -U \"customer-%s-cc-%s-sessid-%s-sesstime-%s:%s\" -o /dev/null -s -L \"%s\"";

    @CommandLine.Option(names = "-c", description = "test count", defaultValue = "3")
    private Integer testCount;

    @CommandLine.Option(names = "-ipc", description = "IP count", defaultValue = "100")
    private Integer ipCount;

    @CommandLine.Option(names = "-session-time", description = "session expired time", defaultValue = "10")
    private Integer sessionTime;

    @CommandLine.Option(names = "-urls", required = true, description = "CURL Url List, use ',' split ")
    private String urls;

    @CommandLine.Option(names = "-pr", description = "proxy address", defaultValue = "pr.oxylabs.io:7777")
    private String proxyAddr;

    @CommandLine.Option(names = "-cc", description = "country，city see:https://developers.oxylabs.io/v/cn/dai-li/zhu-zhai-dai-li/xuan-ze-cheng-shi", defaultValue = "MX")
    private String cc;

    @CommandLine.Option(names = "-u", description = "proxy user", required = true)
    private String user;

    @CommandLine.Option(names = "-p", description = "proxy user pass", required = true)
    private String pass;

    @Override
    public Integer call() throws Exception {
        log.info("test count:{}", testCount);
        log.info("session time:{}", sessionTime);
        log.info("curl urls:{}", urls);
        log.info("proxy address:{}", proxyAddr);
        log.info("cc:{}", cc);
        log.info("user:{}", user);
        log.info("pass:{}", pass);
        AtomicInteger index = new AtomicInteger();
        // 每个 IP 针对  urls进行 curl测试，测试次数根据 test count 控制
        List<DelayInfo> delayInfos = new ArrayList<>();
        getSessionIds(ipCount).forEach(sessid -> {
            // user,cc,sessid,sesstime,pass, url
            Arrays.stream(urls.split(",")).forEach(url -> {
                for (int i = 1; i <= testCount; i++) {
                    String _command = String.format(commond, user, cc, sessid, sessionTime, pass, url);
                    log.debug("command:{}", _command);
                    ShellExecResult res = new ShellExec().exec(_command);
                    if (res.getExitCode() == 0) {
                        try {
                            DelayInfo delayInfo = mapper.readValue(res.getStdout(), DelayInfo.class);
                            delayInfo.setSessid(sessid);
                            delayInfo.setCount(i);
                            delayInfo.setUrl(url);
                            delayInfo.setCurl(_command);
                            // 格式化 延时
                            System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s\n", delayInfo.getSessid(), delayInfo.getCount(), delayInfo.getUrl(), delayInfo.getAppconnect(), delayInfo.getConnect(), delayInfo.getTotal(), delayInfo.getPretransfer(), delayInfo.getStarttransfer());
                            delayInfos.add(delayInfo);
                        } catch (Exception e) {
                            log.error("curl 执行结果 转json报错,", e);
                        }
                    }
                    log.info("c:{}/{}", i, res);
                }
            });
            index.getAndIncrement();
        });
        log.info("curl client count:{},delayInfos:{}", index.get(), delayInfos);
        // 输出到 excel
        writeExcel(delayInfos,cc);
        return 11;
    }

    private List<String> getSessionIds(Integer count) {
        String prefix = "hwwhkM";
        List<String> sessionIds = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            sessionIds.add(prefix + i);
        }
        return sessionIds;
    }

    @SneakyThrows
    private void writeExcel(List<DelayInfo> delayInfos,String cc) {
        HSSFWorkbook workbook = (HSSFWorkbook) ExcelExportUtil.exportExcel(new ExportParams("Curl延迟测试结果", cc +"/区域的延迟测试,每个地址，一个sessid curl 3次", "sheet1"), DelayInfo.class, delayInfos);
        workbook.write(new File("./curl-result.xls"));
    }

}
