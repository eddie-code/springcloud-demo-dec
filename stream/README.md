[TOC]

# 前言

## 2-4 Stream快速入门-集成MQ消费

- 创建 stream-sample 项目, 引入依赖
- 创建监听器 (声明和绑定信道)
- 从RabbitMQ触发消息

### RabbitMQ 界面操作

1. http://192.168.8.240:15672 (guest/guest)
1. 查看 Queue input.anonymous.Z5LyfIlEQtqw3hcJTAhHfA
1. 发送消息
	- 下方 Publish message
	- Payload: 自定义内容
	- 点击按钮 Publish message
	- IDEA控制台就会提示:"message consumed successfully, payload=自定义内容"
	
[Docker部署RabbitMQ](https://blog.eddilee.cn/archives/docker%E9%83%A8%E7%BD%B2rabbitmq%E9%9B%86%E7%BE%A4)

## 2-8 基于发布订阅实现广播功能

- 创建消息生产者Producer服务, 配置消息主题
- 启动多个消费者Consumer节点测试消息广播
- RabbitMQ界面查看广播组（Exchanges）

### 自定义主题 (Topic)

com.example.springcloud.topic.MyTopic
```java
public interface MyTopic {

	/**
	 * Input channel name.
	 */
	String INPUT = "myTopic-consumer";

	/**
	 * Output channel name.
	 */
	String OUTPUT = "myTopic-producer";

	/**
	 * input=消费者
	 */
	@Input(INPUT)
	SubscribableChannel input();

	/**
	 * output=生产者
	 */
	@Output(OUTPUT)
	MessageChannel output();

}
```

### 添加消费者

com.example.springcloud.biz.StreamConsumer
```java
@Slf4j
// 绑定信道
@EnableBinding(
        value = {
                Sink.class,
				MyTopic.class
        }
)
public class StreamConsumer {

    @StreamListener(Sink.INPUT)
	public void consume(Object payload) {
		log.info("message consumed successfully, payload={}", payload);
	}

	@StreamListener(MyTopic.INPUT)
	public void consumeMyMessage(Object payload) {
		log.info("my message consumed successfully, payload={}", payload);
	}

}
```

### 使用配置文件, 绑定生产者和消费者的通道

application.yml
```yaml
# 绑定 Channel 到 broadcast
spring:
  cloud:
    stream:
      bindings:
        myTopic-consumer:  # 消费者绑定
          destination: broadcast # rabbitMq界面显示 Exchange
        myTopic-producer:  # 生产者绑定
          destination: broadcast
```

### 启动与测试

（1） 按不同端口启动
- StreamApplication (63000) :63000/
- StreamApplication (63001) :63001/

（2） Postman (demo - 最简单的生产者消费者)
- POST localhost:63000/send
- Body (x-www-form-urfencoded)
  - body:hello broadcast
- 63000、630001 控制台打印：
  - my message consumed successfully, payload=hello broadcast
  
（3） RabbitMQ WEB
- 打开 http://192.168.8.240:15672
- 查看顶部 Exchanges 下面是否存在 "broadcast"
- 查看 Bindings (每一个Queues都对应后台一个监听队列)
  - broadcast.anonymous.DIILcrP3SvaGEUv6dfiAqQ
  - broadcast.anonymous.UnlUchdPQnaavW5uBIQjEA
- 查看顶部 Queues 是否存在对应 Bindings
  - 点击 broadcast.anonymous.DIILcrP3SvaGEUv6dfiAqQ 
  - 进入后, 点击 Publish message 输入"queues test"
  - 返回 IDEA控制台 就会显示该条 Message
    - my message consumed successfully, payload=queues test


![](.README_images/14f9ae2b.png)

![](.README_images/3ab85703.png)

![](.README_images/6542dfe4.png)


## 2-10 消费组和消息分区详解

### 消费组

前面我们接触的都是广播场景，话说这个广播模式简直就是个围观模式，所有订阅相同主题的消费者都眼巴巴看着生产者发布的消息，一个消息在所有节点都要被消费一遍。如果我只想挑一个节点来消费消息，而且又不能只逮着一只羊来薅羊毛，必须利用负载均衡来分发请求。这个Stream能不能办到呢？
这不就是单播模式吗，那自然不在话下，Stream里的消费组就是专门解决这个问题的。让我们来用一个案例说明它的工作模式：

![](.README_images/fe9075eb.png)

在上面这个例子中，“商品发布”就是一个消息，它被放到了对应的消息队列中，有两拨人马同时盯着这个Topic，这两拨人马各自组成了一个Group，每次有新消息到来的时候，每个Group就派出一个代表去响应，而且是从这个Group中轮流挑选代表（负载均衡），这里的Group也就是我们说的消费者。
在Stream里配置一个消费组非常简单，下一小节我就带大家去做一个Demo。在这里我就先小剧透一点内容好了：

spring.cloud.stream.bindings.group-producer.group=Group-A

看破不说破，这里面是什么含义，且听下节分享。

### 消费分区

消费分区消费组，傻傻分不清楚。这两个名字听起来很像，其实并不是一码事，消费组相当于是每组派一个代表去办事儿，而消费分区相当于是专事专办，也就是说，所有消息都会根据分区Key进行划分，带有相同Key的消息只能被同一个消费者处理。
我们来看下面的消息分区例子：

![](.README_images/319e2d7e.png)

消息分区有一个预定义的分区Key，它是一个SpEL表达式（想想前面哪一章节讲过SpEL？提示换一下，Key Resolver）。我们需要在配置文件中指定分区的总个数N，Stream就会为我们创建N个分区，这里面每个分区就是一个Queue（可以在RabbitMQ管理界面中看到所有的分区队列）。
当商品发布的消息被生产者发布时，Stream会计算得出分区Key，从而决定这个消息应该加入到哪个Queue里面。在这个过程中，每个消费组/消费者仅会连接到一个Queue，这个Queue中对应的消息只能被特定的消费组/消费者来处理。

## 2-11 基于消费组实现轮循单播功能

- 创建 Producer和Consumer
- 配置消费组, 启动两个节点
- RabbitMQ界面单播和广播在Exchange中的不同
- 消费分区的配置项

### 创建 GroupTopic

com.example.springcloud.topic.GroupTopic
```java
public interface GroupTopic {

	/**
	 * Input channel name.
	 */
	String INPUT = "group-consumer";

	/**
	 * Output channel name.
	 */
	String OUTPUT = "group-producer";

	/**
	 * input=消费者
	 */
	@Input(INPUT)
	SubscribableChannel input();

	/**
	 * output=生产者
	 */
	@Output(OUTPUT)
	MessageChannel output();

}
```

com.example.springcloud.biz.controller.DemoController

```java
@Autowired
private GroupTopic groupTopicProducer;  // StreamConsumer 没有绑定前是找不到 标记红色波浪线

@PostMapping("sendToGroup")
public void sendMessageToGroup(@RequestParam(value = "body") String body) {
    groupTopicProducer.output().send(MessageBuilder.withPayload(body).build());
}
```

com.example.springcloud.biz.StreamConsumer
```java
@Slf4j
@EnableBinding(
        value = { GroupTopic.class }
)
public class StreamConsumer {

	@StreamListener(GroupTopic.INPUT)
	public void consumeGroupMessage(Object payload) {
		log.info("Gourp message consumed successfully, payload={}", payload);
	}

}
```

```yaml
---
# 消息分组示例
spring:
  cloud:
    stream:
      bindings:
        group-consumer:  # 消费者绑定
          destination: group-topic
          group: Group-A
        group-producer:  # 生产者绑定
          destination: group-topic

---
# 消费分区配置
spring:
  cloud:
    stream:
      bindings:
        group-consumer: # com.example.springcloud.topic.GroupTopic
          consumer:
            partitioned: true # 打开消费者的消费分区功能
        group-producer:
          producer:
            partition-count: 2 # 两个消息分区
            # SpEL (Key resolver) 可以定义复杂表达式生成Key
            # 我们这里用最简化的配置，只有索引参数为 1 的节点（消费者），才能消费消息 ***
            partition-key-expression: "1"
      instanceCount: 2 # 当前消费者实例总数
      instanceIndex: 1 # 最大值 instanceCount-1，当前实例的索引号 ***
```

### 启动与测试

1. StreamApplication (63000) : Group-A-0
    1. 修改 "spring.cloud.stream.instanceIndex=0"
1. StreamApplication (63001) : Group-A-1
    1. 修改 "spring.cloud.stream.instanceIndex=1"
1. 使用PostMan测试
    1. localhost:63000/sendToGroup
    1. Body (x-www-form-urfencoded)
    1. body:Test 测试 1234

```xml
如何指定:
通过消息分区实现：
请求后 Group-A-1 的控制台会出现打印信息 "Test 测试 1234", 
无论请求多少次都会在 Group-A-1 打印,
为什么呢? 
因为设置了 "partition-key-expression: "1"" 指定消费

TIPS: 比如已经指定了 Group-A-1 端口 63000, 再启动多个 Group-A-1 端口 63001, 然后再次请求, 会发现他们是依次轮询打印到控制台
```

> spring.cloud.stream.bindings.group-consumer.group=Group-A 重点是这个分组配置来区分

## 2-13 Stream+ MQ插件实现延迟消息

- 配置插件, 重启RabbitMQ
- 创建 Producer 和 Consumer, 配置exchange-type
- 添加Message Header传递延迟时间
- 启动查看效果

### RabbitMQ部分

#### 部署与安装插件
- [Docker - rabbitmq:3.6.15 部署](https://blog.eddilee.cn/archives/docker%E9%83%A8%E7%BD%B2rabbitmq%E9%9B%86%E7%BE%A4) 
- [Docker - rabbitmq:3.6.15 部署 (备份地址)](https://blog.csdn.net/eddielee9217/article/details/113713318) 
- [延迟消息 - 官方插件版本](https://www.rabbitmq.com/community-plugins.html)
- [参考资料 - 安装插件](https://blog.csdn.net/wangming520liwei/article/details/103352440)

(1) &nbsp; 终端直接下载 (部署的版本是：3.6.15)
```shell script
[root@k8s-master1 opt]# wget https://dl.bintray.com/rabbitmq/community-plugins/3.6.x/rabbitmq_delayed_message_exchange/rabbitmq_delayed_message_exchange-20171215-3.6.x.zip
--2021-03-01 22:28:32--  https://dl.bintray.com/rabbitmq/community-plugins/3.6.x/rabbitmq_delayed_message_exchange/rabbitmq_delayed_message_exchange-20171215-3.6.x.zip
Resolving dl.bintray.com (dl.bintray.com)... 44.239.142.179, 52.10.12.153, 52.32.247.225, ...
Connecting to dl.bintray.com (dl.bintray.com)|44.239.142.179|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 29853 (29K) [application/zip]
Saving to: ‘rabbitmq_delayed_message_exchange-20171215-3.6.x.zip’

100%[==============================================================================================================================================================================================>] 29,853      73.7KB/s   in 0.4s   
```

(2) &nbsp; 解压
```shell script
[root@k8s-master1 opt]# unzip rabbitmq_delayed_message_exchange-20171215-3.6.x.zip
Archive:  rabbitmq_delayed_message_exchange-20171215-3.6.x.zip
  inflating: rabbitmq_delayed_message_exchange-20171215-3.6.x.ez  
```

(3) &nbsp; 拷贝到容器里
```shell script
[root@k8s-master1 ~]# docker cp /opt/rabbitmq_delayed_message_exchange-20171215-3.6.x.ez myrabbit1:/opt
```

(4) &nbsp; 进入容器
```shell script
[root@k8s-master1 opt]# docker exec -it myrabbit1 bash

root@rabbit1:/# cp /opt/rabbitmq_delayed_message_exchange-20171215-3.6.x.ez /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.15/plugins
```

(5) &nbsp; 从 opt 到插件 plugins 里
```shell script
root@rabbit1:/usr/lib/rabbitmq/lib/rabbitmq_server-3.6.15/plugins# cd /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.15/sbin

root@rabbit1:/usr/lib/rabbitmq/lib/rabbitmq_server-3.6.15/sbin# rabbitmq-plugins enable rabbitmq_delayed_message_exchange
The following plugins have been enabled:
  rabbitmq_delayed_message_exchange

Applying plugin configuration to rabbit@rabbit1... started 1 plugin.
```

(6) &nbsp; 查看 rabbitmq_delayed_message_exchange 是否安装成功
```shell script
root@rabbit1:/usr/lib/rabbitmq/lib/rabbitmq_server-3.6.15/sbin# rabbitmq-plugins list
 Configured: E = explicitly enabled; e = implicitly enabled
 | Status:   * = running on rabbit@rabbit1
 |/
[e*] amqp_client                       3.6.15
[e*] cowboy                            1.0.4
[e*] cowlib                            1.0.2
[  ] rabbitmq_amqp1_0                  3.6.15
[  ] rabbitmq_auth_backend_ldap        3.6.15
[  ] rabbitmq_auth_mechanism_ssl       3.6.15
[  ] rabbitmq_consistent_hash_exchange 3.6.15
[E*] rabbitmq_delayed_message_exchange 20171215-3.6.x
[  ] rabbitmq_event_exchange           3.6.15
[  ] rabbitmq_federation               3.6.15
[  ] rabbitmq_federation_management    3.6.15
[  ] rabbitmq_jms_topic_exchange       3.6.15
[E*] rabbitmq_management               3.6.15
[e*] rabbitmq_management_agent         3.6.15
[  ] rabbitmq_management_visualiser    3.6.15
[  ] rabbitmq_mqtt                     3.6.15
[  ] rabbitmq_random_exchange          3.6.15
[  ] rabbitmq_recent_history_exchange  3.6.15
[  ] rabbitmq_sharding                 3.6.15
[  ] rabbitmq_shovel                   3.6.15
[  ] rabbitmq_shovel_management        3.6.15
[  ] rabbitmq_stomp                    3.6.15
[  ] rabbitmq_top                      3.6.15
[  ] rabbitmq_tracing                  3.6.15
[  ] rabbitmq_trust_store              3.6.15
[e*] rabbitmq_web_dispatch             3.6.15
[  ] rabbitmq_web_mqtt                 3.6.15
[  ] rabbitmq_web_mqtt_examples        3.6.15
[  ] rabbitmq_web_stomp                3.6.15
[  ] rabbitmq_web_stomp_examples       3.6.15
[  ] sockjs                            0.3.4
```

(7) &nbsp; Reboot Rabbitmq
```shell script
方式一
docker restart myrabbit1 myrabbit2 myrabbit3

方式二
docker exec -it myrabbit1 bash
rabbitmqctl stop
rabbitmq-server
```

(8) &nbsp; 访问 WEB UI
```shell script
http://192.168.8.240:15672
```

![](.README_images/8967dfec.png)

### Quick start

(1) &nbsp; 创建Topic
```java
public interface DelayedTopic {

	/**
	 * Input channel name.
	 */
	String INPUT = "delayed-consumer";

	/**
	 * Output channel name.
	 */
	String OUTPUT = "delayed-producer";

	/**
	 * input=消费者
	 */
	@Input(INPUT)
	SubscribableChannel input();

	/**
	 * output=生产者
	 */
	@Output(OUTPUT)
	MessageChannel output();

}
```

(2) &nbsp; 创建请求接口

com.example.springcloud.biz.controller.DemoController
```java
@PostMapping("sendDM")
public void sendDelayedMessage(@RequestParam(value = "body") String body,
        @RequestParam(value = "seconds") Integer seconds) {

    MessageBean msg = new MessageBean();
    msg.setPayload(body);

    log.info("[{}]秒后准备发送延迟消息",seconds);

    delayedTopicProducer.output().send(
            MessageBuilder.withPayload(msg)
                    .setHeader("x-delay", seconds * 1000)
                    .build()
    );
}
```

(3) &nbsp; 消费者创建打印MessageBaen信息

com.example.springcloud.biz.StreamConsumer
```java
@StreamListener(DelayedTopic.INPUT)
public void consumeDelayedMessage(MessageBean bean) {
    log.info("Delayed message consumed successfully, payload={}", bean.getPayload());
}
```

(4) &nbsp; application.yml

```yaml
# 延迟消息配置
spring:
  cloud:
    stream:
      bindings:
        delayed-consumer:
          destination: delayed-topic
        delayed-producer:
          destination: delayed-topic
      rabbit:
        bindings:
          delayed-producer:
            producer:
              delayed-exchange: true # 延迟队列
```

(4) &nbsp; PostMan请求测试

![](.README_images/e54317ba.png)

控制台打印 (38-23) 刚好 15s
```text
2021-03-03 14:29:23.172  INFO 16512 --- [io-63000-exec-1] c.e.s.biz.controller.DemoController      : [15]秒后准备发送延迟消息
2021-03-03 14:29:23.184  INFO 16512 --- [io-63000-exec-1] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [192.168.8.240:5672]
2021-03-03 14:29:23.194  INFO 16512 --- [io-63000-exec-1] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory.publisher#3d78cf08:0/SimpleConnection@4898ec6c [delegate=amqp://guest@192.168.8.240:5672/, localPort= 11225]
2021-03-03 14:29:23.197  INFO 16512 --- [io-63000-exec-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (input.anonymous.FLQqBEtsQ_-ti45RQP4C5g) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
2021-03-03 14:29:23.197  INFO 16512 --- [io-63000-exec-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (broadcast.anonymous.sVMurJRTTPmzbJeiv2_YCA) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
2021-03-03 14:29:23.197  INFO 16512 --- [io-63000-exec-1] o.s.amqp.rabbit.core.RabbitAdmin         : Auto-declaring a non-durable, auto-delete, or exclusive Queue (delayed-topic.anonymous.zRO1l6z8R8yoRe-iHDcfMA) durable:false, auto-delete:true, exclusive:true. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.

2021-03-03 14:29:38.244  INFO 16512 --- [8yoRe-iHDcfMA-1] c.e.springcloud.biz.StreamConsumer       : Delayed message consumed successfully, payload=欢迎关注：https://blog.csdn.net/eddielee9217
```

![](.README_images/540cf021.png)