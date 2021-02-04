package com.example.springcloud.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springcloud.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName JwtService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 17:14
 * @modified by
 */
@Slf4j
@Service
public class JwtService {

    /**
     * 生产环境不能这样写, 通常外部引用. 如果给别人知道会有安全隐患
     */
    private static final String KEY = "changIt";
    private static final String ISSUER = "eddie";

    private static final String USER_NAME = "username";
    private static final long TOKEN_EXP_TIME = 60 * 1000; // 60秒

    /**
     * 生成token
     * @param account 账户
     * @return str
     */
    public String token(Account account) {
        Date now = new Date();
        // 算法
        Algorithm algorithm = Algorithm.HMAC256(KEY);

        String token = JWT.create()
                .withIssuer(ISSUER) // 发行方,解密的时候依然要验证,即便拿到了key不知道发行方也无法解密
                .withIssuedAt(now) // 这个key是在什么时间点生成的
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))// 过期时间
                .withClaim(USER_NAME, account.getUsername()) // 传入username
//                .withClaim("ROLE","roleName") // 企业级别应用基本都会传入 ROLE 权限
                .sign(algorithm); // 用前面的算法签发

		log.info("jwt generated user={}, token={}", account.getUsername(), token);
		return token;
    }

    /**
     * 校验token
     * @param token str
     * @param username str
     * @return b
     */
    public boolean verify(String token, String username) {
        log.info("verifying jwt - username={}", username);

        try {
            // 加密和解密要一样
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            // 构建一个验证器:验证JWT的内容,是个接口
            JWTVerifier verifier = JWT.require(algorithm)
                    // 前面加密的内容都可以验证
                    .withIssuer(ISSUER)
                    .withClaim(USER_NAME, username)
                    .build();
            // 这里有任何错误就直接异常了
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("auth failed", e);
            return false;
        }

    }

}
