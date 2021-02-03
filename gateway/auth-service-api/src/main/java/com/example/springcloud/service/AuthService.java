package com.example.springcloud.service;

import com.example.springcloud.entity.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName AuthService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 16:21
 * @modified by
 */
@FeignClient("auth-service")
public interface AuthService {

    /**
     *  创建token
     * @param username str
     * @param password str
     * @return AuthResponse
     */
	@PostMapping("/login")
	@ResponseBody
	public AuthResponse login(@RequestParam("username") String username,
							  @RequestParam("password") String password);

    /**
     *  验证token
     * @param token str
     * @param username name
     * @return AuthResponse
     */
	@GetMapping("/verify")
	public AuthResponse verify(@RequestParam("token") String token,
							   @RequestParam("username") String username);

    /**
     * 刷新token
     * @param refresh str
     * @return AuthResponse
     */
	@PostMapping("/refresh")
	@ResponseBody
	public AuthResponse refresh(@RequestParam("refresh") String refresh);

}
