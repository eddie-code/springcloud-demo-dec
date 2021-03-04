package com.example.springcloud.biz;

import com.example.springcloud.topic.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.biz
 * @ClassName StreamConsumer
 * @blog blog.eddilee.cn
 * @description 消费者
 * @date created in 2021-02-25 15:21
 * @modified by
 */
@Slf4j
// 绑定信道
@EnableBinding(
        value = {
                Sink.class,
				MyTopic.class,
				GroupTopic.class,
				DelayedTopic.class,
				ErrorTopic.class,
				RequeueTopic.class
        }
)
public class StreamConsumer {

    @StreamListener(Sink.INPUT)
	public void consume(Object payload) {
		log.info("message consumed successfully, payload={}", payload);
	}

	/**
	 * 自定义消息广播
	 * @param payload
	 */
	@StreamListener(MyTopic.INPUT)
	public void consumeMyMessage(Object payload) {
		log.info("My message consumed successfully, payload={}", payload);
	}

	/**
	 * 消息分组 & 消费分区示例
	 * @param payload
	 */
	@StreamListener(GroupTopic.INPUT)
	public void consumeGroupMessage(Object payload) {
		log.info("Gourp message consumed successfully, payload={}", payload);
	}

	/**
	 * 延迟消息示例
	 * @param bean
	 */
	@StreamListener(DelayedTopic.INPUT)
	public void consumeDelayedMessage(MessageBean bean) {
		log.info("Delayed message consumed successfully, payload={}", bean.getPayload());
	}

	/**
	 * 线程安全的计数器，起始值=1
	 */
	private AtomicInteger count = new AtomicInteger(1);

	/**
	 * 异常重试（单机版）
	 * @param bean
	 */
	@StreamListener(ErrorTopic.INPUT)
	public void consumeErrorMessage(MessageBean bean) {
		log.info("你还好吗？");
		// 每次都自增一 当你被三整除就放行
		boolean b = count.incrementAndGet() % 3 == 0;
		System.out.println(b);
		if (b) {
			log.info("很好，谢谢。你呢？");
			// 成功消费以后, 就会清零
			count.set(0);
		} else {
			log.info("你怎么回事啊？");
			throw new RuntimeException("我不好~");
		}
	}

	/**
	 * 异常重试（联机版-重新入列）
	 * 
	 * @param bean
	 */
	@StreamListener(RequeueTopic.INPUT)
	public void requeueErrorMessage(MessageBean bean) {
		log.info("Are you OK?");
		try {
			Thread.sleep(3000L);
		} catch (Exception e) {
		}
		 throw new RuntimeException("I'm not OK");
	}

}
