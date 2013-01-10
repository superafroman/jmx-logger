jmx-logger
==========

JMX log appender for log4j.

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration debug="false" xmlns:log4j="http://jakarta.apache.org/log4j/">

  <appender name="JMX" class="com.which.logger.jmx.JmxLogAppender">
    <param name="ObjectName" value="com.superafroman.example:service=Logging,type=JmxLogAppender"/>
    <param name="Threshold" value="WARN" />
    <layout class="org.apache.log4j.PatternLayout">
       <param name="ConversionPattern" value="%d{ISO8601} %-5p[%t] %c{1} - %m%n"/>
    </layout>
  </appender>

  <appender name="Console" class="org.apache.log4j.ConsoleAppender">
    <layout class="org.apache.log4j.PatternLayout">
        <param name="ConversionPattern" value="%d{ISO8601} %-5p[%t] %c{1} - %m%n"/>
        </layout>
  </appender>

  <root>
    <priority value="DEBUG" />
    <appender-ref ref="JMX" />
    <appender-ref ref="Console" />
  </root>
</log4j:configuration>
```
