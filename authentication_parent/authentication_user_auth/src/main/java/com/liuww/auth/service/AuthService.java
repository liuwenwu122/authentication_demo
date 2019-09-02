package com.liuww.auth.service;

import com.alibaba.fastjson.JSON;

import com.liuww.auth.domain.auth.AuthToken;
import com.liuww.auth.domain.auth.UserTokenStore;
import com.liuww.auth.entity.ResponseCode;
import com.liuww.auth.apilist.RegisterServiceList;
import com.liuww.auth.execption.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc: 用户认证服务
 */
@Service
public class AuthService {

    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    @Value("${auth.clientId}")
    String clientId;

    @Value("${auth.clientSecret}")
    String clientSecret;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    RestTemplate restTemplate;


    /**
     * 用户认证申请令牌，将令牌存储到redis
     */
    public AuthToken login(String username, String password) {

        //请求spring security申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            throw new CustomException(ResponseCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //用户身份信息令牌
        String access_token = authToken.getAccess_token();

        //将令牌存储到redis
        String jsonString = JSON.toJSONString(authToken);
        boolean result = this.saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!result) {
            throw new CustomException(ResponseCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;

    }

    /**
     * 存储到令牌到redis
     *
     * @param access_token 用户身份令牌
     * @param content      内容就是AuthToken对象的内容
     * @param ttl          过期时间
     * @return
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 认证服务访问 http://localhost:40400/auth/oauth/token 申请令牌
     *
     * @param username     账户名
     * @param password     密码
     * @param clientId     客户端ID
     * @param clientSecret 客户端密码
     * @return token令牌
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {

        //从eureka中获取认证服务的一个实例的地址(貌似就是认证服务它自己服务地址)
        ServiceInstance serviceInstance = loadBalancerClient.choose(RegisterServiceList.SERVICE_UCENTER_AUTH);

        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();
        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //定义请求头Header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();

        // httpBasic请求认证信息 = clientId(访问ID) + clientSecret(访问密码)
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        //定义body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);

        // 将请定义header和body放入请求体
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        // 获取申请的令牌信息
        Map bodyMap = exchange.getBody();

        // 判断所携带信息有一个为null，则返回null
        if (bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null) {
            return null;
        }
        AuthToken authToken = new AuthToken();
        // 用户身份令牌
        authToken.setAccess_token((String) bodyMap.get("jti"));
        // 刷新令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));
        // jwt令牌
        authToken.setJwt_token((String) bodyMap.get("access_token"));

        return authToken;
    }

    /**
     * 获取httpBasic的串
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    /**
     * 用户身份访问令牌 获取JWT
     * @param auth_id
     * @return
     */
    public AuthToken getUserToken(String auth_id) {

        String access_token = "user_token:" + auth_id;
        String userTokenString = stringRedisTemplate.opsForValue().get(access_token);

        try {
            AuthToken userToken = JSON.parseObject(userTokenString, AuthToken.class);
            return userToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户身份访问令牌 删除JWT
     * @param access_token
     * @return
     */
    public boolean deleteToken(String access_token) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.delete(key);
        return true;
    }
}
