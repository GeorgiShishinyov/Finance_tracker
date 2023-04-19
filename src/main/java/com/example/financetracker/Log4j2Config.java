package com.example.financetracker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class Log4j2Config {

    @Bean
    public LoggerContext loggerContext() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("Log4j2Config");

        // Create a Console Appender
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Console", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"));
        builder.add(appenderBuilder);

        // Create a File Appender
        AppenderComponentBuilder fileAppenderBuilder = builder.newAppender("File", "FILE")
                .addAttribute("fileName", "logs/app.log")
                .addAttribute("append", true);
        fileAppenderBuilder.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"));
        builder.add(fileAppenderBuilder);

        // Create a Logger configuration
        builder.add(builder.newLogger("org.springframework", Level.INFO)
                .add(builder.newAppenderRef("Console"))
                .add(builder.newAppenderRef("File")) // Add this line to reference the File appender
                .addAttribute("additivity", false));

        // Add the Console and File appenders to the root logger
        builder.add(builder.newRootLogger(Level.INFO)
                .add(builder.newAppenderRef("Console"))
                .add(builder.newAppenderRef("File"))); // Add this line to reference the File appender

        // Initialize the LoggerContext with the configuration and return it
        LoggerContext context = Configurator.initialize(builder.build());
        return context;
    }
}
