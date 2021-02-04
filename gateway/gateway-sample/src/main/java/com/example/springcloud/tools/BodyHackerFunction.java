package com.example.springcloud.tools;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.tools
 * @ClassName BodyHackerFunction
 * @blog blog.eddilee.cn
 * @description 代理模式
 * @date created in 2021-02-04 11:01
 * @modified by
 */
public interface BodyHackerFunction extends BiFunction<ServerHttpResponse, Publisher<? extends DataBuffer>, Mono<Void>> {
}
