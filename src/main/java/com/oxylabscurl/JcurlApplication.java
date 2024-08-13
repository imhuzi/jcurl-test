package com.oxylabscurl;

import com.oxylabscurl.command.JcurlCommand;
import com.oxylabscurl.command.PcurlCommand;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@Slf4j
@CommandLine.Command(
        name = "helper",
        description = "基于CURL的网络时延测试对比工具",
        mixinStandardHelpOptions = true,
        version = "1.0.1",
        subcommands = {JcurlCommand.class, PcurlCommand.class}
)
public class OxylabsCurlApplication implements ApplicationRunner{


    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(OxylabsCurlApplication.class, args)));
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        CommandLine commandLine = new CommandLine(this);
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.execute(args.getSourceArgs());
    }
}
