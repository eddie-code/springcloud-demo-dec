package com.example.springcloud.topic;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.topic
 * @ClassName DelayedTopic
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-27 11:56
 * @modified by
 */
public interface ErrorTopic {

	/**
	 * Input channel name.
	 */
	String INPUT = "error-consumer";

	/**
	 * Output channel name.
	 */
	String OUTPUT = "error-producer";

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
