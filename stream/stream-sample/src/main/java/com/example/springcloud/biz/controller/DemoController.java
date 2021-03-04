package com.example.springcloud.biz.controller;

import com.example.springcloud.biz.MessageBean;
import com.example.springcloud.topic.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.biz.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-27 12:06
 * @modified by
 */
@Slf4j
@RestController
public class DemoController {

	@Autowired
	private MyTopic producer;

    /**
     * StreamConsumer 没有绑定前是找不到 标记红色波浪线
     */
	@Autowired
	private GroupTopic groupTopicProducer;

	@Autowired
	private DelayedTopic delayedTopicProducer;

	@Autowired
	private ErrorTopic errorTopicProducer;	
	
	@Autowired
	private RequeueTopic requeueTopicProducer;

	@Autowired
	private DlqTopic dlqTopicProducer;

	/**
	 * 简单广播消息
	 * 
	 * @param body
	 */
	@PostMapping("send")
	public void sendMessage(@RequestParam(value = "body") String body) {
		producer.output().send(MessageBuilder.withPayload(body).build());
	}

	/**
	 * 消息分组和消息分区
	 * 
	 * @param body
	 */
	@PostMapping("sendToGroup")
	public void sendMessageToGroup(@RequestParam(value = "body") String body) {
		groupTopicProducer.output().send(MessageBuilder.withPayload(body).build());
	}

	/**
	 * 延迟消息
	 * 
	 * @param body
	 * @param seconds
	 */
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

	/**
	 * 异常重试（单机版）
	 * 
	 * @param body
	 */
	@PostMapping("sendError")
	public void sendErrorMessage(@RequestParam(value = "body") String body) {
		MessageBean msg = new MessageBean();
		msg.setPayload(body);
		errorTopicProducer.output().send(
				MessageBuilder.withPayload(msg).build()
		);
	}

	/**
	 * 异常重试（联机版 - 重新入列）
	 * 
	 * @param body
	 */
	@PostMapping("requeue")
	public void sendErrorMessageToMq(@RequestParam(value = "body") String body) {
		MessageBean msg = new MessageBean();
		msg.setPayload(body);
		requeueTopicProducer.output().send(MessageBuilder.withPayload(msg).build());
	}

	/**
	 * 死信队列测试
	 * 
	 * @param body
	 */
	@PostMapping("dlq")
	public void sendMessageToDlq(@RequestParam(value = "body") String body) {
		MessageBean msg = new MessageBean();
		msg.setPayload(body);
		dlqTopicProducer.output().send(MessageBuilder.withPayload(msg).build());
	}

}
