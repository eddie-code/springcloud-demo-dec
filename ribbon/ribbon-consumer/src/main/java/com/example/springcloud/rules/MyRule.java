package com.example.springcloud.rules;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.rules
 * @ClassName MyRule
 * @blog blog.eddilee.cn
 * @description 一致性哈希算法 - 负载均衡
 * @date created in 2021-01-11 16:22
 * @modified by
 */
@NoArgsConstructor
public class MyRule extends AbstractLoadBalancerRule implements IRule {

	/**
	 * 虚拟节点数
	 */
	public static final int VIRTUAL_NODE_NUM = 8;

	@Override
	public void initWithNiwsConfig(IClientConfig iClientConfig) {

	}

	@Override
	public Server choose(Object o) {
		HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
		String uri = request.getServletPath() + "?" + request.getQueryString();
		// hashCode() = int;
		return route(uri.hashCode(), getLoadBalancer().getAllServers());
	}

	public Server route(int hashId, List<Server> addressList) {
		if (CollectionUtils.isEmpty(addressList)) {
			return null;
		}
		// 首先是将node定位到圆上,我们以 hash - address方式定位
		// 因为后面需要获取离jobId最近node所以将数据放入到TreeMap中
		TreeMap<Long, Server> address = new TreeMap<>();
		addressList.stream().forEach(e -> {
			// 将每个node虚拟化为8个节点
			for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
//				long hash = hash("SHARD-" + address + "-NODE-" + i);
//				address.put(hash, e);

				 long hash = hash(e.getId() + i);
				 address.put(hash, e);
			}
		});

		long hash = hash(String.valueOf(hashId));

		// 这里是顺时针转动jobHash寻找node的策略,其实就是寻找node哈希值大于等于jobId哈希值的最近一个node
		SortedMap<Long, Server> last = address.tailMap(hash);

		// 当request URL的hash值大于任意一个服务器对应的hashKey.
		// 取address中的第一个节点

		if (last.isEmpty()) {
			address.firstEntry().getValue();
		}

//		if (!last.isEmpty()) {
//			return last.get(last.firstKey());
//		}

		// 如果请求落在最大一组hash上,那么就返回第一个node
		return last.get(last.firstKey());
	}

	public long hash(String key) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}
		byte[] keyByte = null;
		keyByte = key.getBytes(StandardCharsets.UTF_8);

		md5.update(keyByte);
		byte[] digest = md5.digest();

		long hashCode = ((long) (digest[3] & 0xFF) << 24)
				| ((long) (digest[2] & 0xFF) << 16)
				| ((long) (digest[1] & 0xFF) << 8)
				| ((long) (digest[0] & 0xFF));

		return hashCode & 0xffffffffL;
	}

}
