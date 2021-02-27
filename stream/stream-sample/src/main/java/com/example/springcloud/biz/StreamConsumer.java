package com.example.springcloud.biz;

import com.example.springcloud.topic.MyTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

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
