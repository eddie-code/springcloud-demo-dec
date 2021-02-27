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