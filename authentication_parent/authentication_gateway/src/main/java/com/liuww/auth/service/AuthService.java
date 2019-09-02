package com.liuww.auth.service;

import com.liuww.auth.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */

@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 从请求头获取jwt令牌
     *
     * @param request
     * @return
     */
    public String getJwtFormHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return null;
        }
        if (!authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring(7);
    }


    /**
     * 从cookie取出身份令牌
     *
     * @return
     */
    public String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookies = CookieUtil.readCookie(request, "auth_id");
        if (!CollectionUtils.isEmpty(cookies) && !org.apache.commons.lang3.StringUtils.isEmpty(cookies.get("auth_id"))) {
            return cookies.get("auth_id");
        }
        return null;
    }


    /**
     * 用身份令牌去redis查询token信息
     *
     * @param access_token
     * @return
     */
    public String authTokenFormRedis(String access_token) {
        String key = "user_token:" + access_token;
        try {
            String JwtToken = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(JwtToken)) {
                return JwtToken;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
