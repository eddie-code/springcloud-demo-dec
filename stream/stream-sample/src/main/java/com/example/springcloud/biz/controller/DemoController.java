package com.example.springcloud.biz.controller;

import com.example.springcloud.topic.MyTopic;
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

    @PostMapping("send")
    public void sendMessage(@RequestParam(value = "body") String body) {
        producer.output().send(MessageBuilder.withPayload(body).build());
    }

}
