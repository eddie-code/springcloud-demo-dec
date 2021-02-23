[TOC]

# 前言

## 1-5 整合Sleuth追踪调用链路

- 创建 sleuth-traceA 和 sleuth-traceB, 添加 Sleuth 依赖
- 调用请求链路, 查看log中的信息
- 采样率设置
- 启动测试
  - EurekaServerApplication :20000/
  - SleuthTrace1Application :62000/
  - SleuthTrace2Application :62001/
  - GET http://localhost:62001/traceB  | 查看log
  - GET http://localhost:62000/traceA  | 查看log, 会出现 [里面某些字符串一致]


## 1-11 Sleuth集成ELK实现日志检索-1

ELK是什么
- Elasticsearch 搜索、日志信息持久化
- Logstash 日志收集和过滤
- Kibana 通过前端显示给用户搜索

ELK流程<br>

Logstash (Log信息收集) 
        <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ---> ElasticSearch (存储Log信息提供搜索)
        <br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ---> Kibana (Log信息的查询、报表等)

### 下载ELK镜像

- 通过docker安装elk镜像（当前版本：v7.10.0）
- 创建Docker容器, 取名为elk, 指定ELK三个组件的端口
- 修改logstash接收日志的方式 (docker里面修改)

elk_commands
```xml
1. 下载镜像（时间很久，耐心要足）：
docker pull sebp/elk

2. 创建Docker容器（只用在第一次使用的时候才创建）
docker run -d -p 5601:5601 -p 9200:9200 -p 5044:5044 -e ES_MIN_MEM=128m  -e ES_MAX_MEM=1024m -it --name elk sebp/elk

3. 进入docker容器：
docker exec -it elk /bin/bash

4. 修改配置文件
配置文件的位置：/etc/logstash/conf.d/02-beats-input.conf
将其中的内容都删掉，替换成下面的配置
input {
    tcp {
        port => 5044
        codec => json_lines

    }

}
output{
    elasticsearch {
    hosts => ["localhost:9200"]

    }

}

5.	重启docker容器（大概等5-10分钟，等待服务重启）
docker restart elk

6. 访问Kibana
http://localhost:5601/

左侧按钮：
Stack Management
Index patterns
Create index pattern  
   -----> 打开按钮 Include system and hidden indices (包括系统和隐藏索引)
   -----> Index pattern name 输入通配符 '*' 
      -----> Next Step
      -----> Time field 选择 @timestamp
      -----> 点击：Create index pattern
Discover

7. 错误汇总
错题提示：
[1]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
解决办法：
vim /etc/sysctl.conf
vm.max_map_count=262144
reboot
docker stop elk && docker rm elk
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -e ES_MIN_MEM=128m  -e ES_MAX_MEM=1024m -it --name elk sebp/elk
```

## 1-12 Sleuth集成ELK实现日志检索-2

### Sleuth集成ELK

- 引入Logstash依赖到Sleuth项目中
- 配置日志文件, 输出JSON格式到日志到Logstash

sleuth-traceA 与 sleuth-traceB 添加依赖
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>5.2</version>
</dependency>
```

sleuth-traceA 与 sleuth-traceB 的 logback-spring.xml 添加 logstash 配置
```xml
<!-- Logstash -->
<!-- 为logstash输出的JSON格式的Appender -->
<appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>192.168.8.246:5044</destination>
    <!-- 日志输出编码 -->
    <encoder
            class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp>
                <timeZone>UTC</timeZone>
            </timestamp>
            <pattern>
                <pattern>
                    {
                    "severity": "%level",
                    "service": "${springAppName:-}",
                    "trace": "%X{X-B3-TraceId:-}",
                    "span": "%X{X-B3-SpanId:-}",
                    "exportable": "%X{X-Span-Export:-}",
                    "pid": "${PID:-}",
                    "thread": "%thread",
                    "class": "%logger{40}",
                    "rest": "%message"
                    }
                </pattern>
            </pattern>
        </providers>
    </encoder>
</appender>

<!-- 日志输出级别 -->
<root level="INFO">
    <appender-ref ref="console" />
    <appender-ref ref="logstash" />
</root>
```

### 访问Kibana 
http://localhost:5601/ <br>
Discover --> 选择 '*'

### 请求与查询
1. 清空控制台
1. 请求 http://localhost:62000/traceA
1. 复制控制台的 traceId 到 Kibana页面上查询
    1. 精准查询关键字和值："trace: 32deeb6a183f1e71"



<br>

[GitHub](https://github.com/eddie-code) <br>
[博客园](https://www.cnblogs.com/EddieBlog) <br>
[CSDN](https://blog.csdn.net/eddielee9217) <br>
[自建博客](https://blog.eddilee.cn/s/about) <br>