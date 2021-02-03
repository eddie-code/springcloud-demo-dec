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
                .withIssuer(ISSUER) // 发行者, 这个在生产环境也是需要加密的
                .withIssuedAt(now) // 当前时间
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))// 过期时间
                .withClaim(USER_NAME, account.getUsername())
//                .withClaim("ROLE","") // 企业级别应用基本都会传入 ROLE 权限
                .sign(algorithm);

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
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .withClaim(USER_NAME, username)
                    .build();

            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("auth failed", e);
            return false;
        }

    }

}
