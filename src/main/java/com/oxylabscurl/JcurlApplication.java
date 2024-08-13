package com.oxylabscurl;

import com.oxylabscurl.command.TestCommand;
import com.oxylabscurl.command.PtestCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@Slf4j
@CommandLine.Command(
        name = "jcurl",
        description = "基于CURL的网络时延测试对比工具",
        mixinStandardHelpOptions = true,
        version = "1.0.1",
        subcommands = {TestCommand.class, PtestCommand.class}
)
public class JcurlApplication implements ApplicationRunner{


    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(JcurlApplication.class, args)));
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.execute(args.getSourceArgs());
    }
}
