package com.oxylabscurl.command;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxylabscurl.shell.DelayInfo;
import com.oxylabscurl.shell.IpInfo;
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
    static ObjectMapper mapper = new ObjectMapper();
        static {
	mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	mapper.configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION,false);
	mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

    // user,cc,sessid,sesstime,pass, url
    private String commond = "curl -w \"@curl-format.txt\" -x pr.oxylabs.io:7777 -U \"customer-%s-cc-%s-sessid-%s-sesstime-%s:%s\" -o /dev/null -s -L \"%s\"";

    private String ipInfoCommond = "curl -x pr.oxylabs.io:7777 -U \"customer-%s-cc-%s-sessid-%s-sesstime-%s:%s\" -s -L \"%s\"";

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
	    // get ipInfo
	    IpInfo ipInfo = getIpInfo(sessid);
            Arrays.stream(urls.split(",")).forEach(url -> {
		String[] urlArr = url.split(":", 2);
		String _url = urlArr[1];
                for (int i = 1; i <= testCount; i++) {
                    String _command = String.format(commond, user, cc, sessid, sessionTime, pass, _url);
                    log.debug("command:{}", _command);
                    ShellExecResult res = new ShellExec().exec(_command);
                    if (res.getExitCode() == 0) {
                        try {
                            DelayInfo delayInfo = mapper.readValue(res.getStdout(), DelayInfo.class);
                            delayInfo.setSessid(sessid);
                            delayInfo.setCount(i);
                            delayInfo.setUrl(_url);
                            delayInfo.setCurl(_command);
			    delayInfo.setTarget(urlArr[0]);
			    if (ipInfo != null) {
			       delayInfo.setIp(ipInfo.getIp());
			       delayInfo.setCity(ipInfo.getCity());
			       delayInfo.setRegion(ipInfo.getRegion());
			       delayInfo.setCountry(ipInfo.getCountry());
			       delayInfo.setLoc(ipInfo.getLoc());
			       delayInfo.setOrg(ipInfo.getOrg());
			       delayInfo.setPostal(ipInfo.getPostal());
			       delayInfo.setTimezone(ipInfo.getTimezone());
			    }
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

        private IpInfo getIpInfo(String sessid) {
		String _command = String.format(ipInfoCommond, user, cc, sessid, sessionTime, pass, "https://ipinfo.io");
		log.debug("ipInfo command:{}", _command);
		ShellExecResult res = new ShellExec().exec(_command);
		if (res.getExitCode() == 0) {
		try {
	             return mapper.readValue(res.getStdout(), IpInfo.class);
		} catch (Exception e) {
														                log.error("curl ipInfo exec to json error:{}", res, e);
   																            }
		}
	        return null;
	}

}
