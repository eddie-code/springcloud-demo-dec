package com.example.springcloud.tools;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.tools
 * @ClassName BodyHackerHttpResponseDecorator
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-04 11:08
 * @modified by
 */
public class BodyHackerHttpResponseDecorator extends ServerHttpResponseDecorator {

    /**
     * 负责具体写入Body内容的代理类
     */
    private BodyHackerFunction delegate = null;

    public BodyHackerHttpResponseDecorator(BodyHackerFunction bodyHandler, ServerHttpResponse delegate) {
        super(delegate);
        this.delegate = bodyHandler;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return delegate.apply(getDelegate(), body);
    }
}
