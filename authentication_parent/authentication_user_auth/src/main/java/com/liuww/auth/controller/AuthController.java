package com.liuww.auth.controller;

import com.liuww.auth.domain.auth.AuthToken;
import com.liuww.auth.domain.auth.JwtResponseResult;
import com.liuww.auth.domain.auth.LoginRequest;
import com.liuww.auth.entity.ResponseCode;
import com.liuww.auth.entity.ResponseResult;
import com.liuww.auth.execption.CustomException;
import com.liuww.auth.service.AuthService;
import com.liuww.auth.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/")
public class AuthController {


    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @Autowired
    private AuthService authService;


    /**
     * 用户登录申请令牌
     *
     * @param loginRequest
     * @return
     */
    @PostMapping("/userlogin")
    public ResponseResult login(LoginRequest loginRequest) {

        if (ObjectUtils.isEmpty(loginRequest) || StringUtils.isEmpty(loginRequest.getUsername())) {
            throw new CustomException(ResponseCode.AUTH_USERNAME_NONE);
        }

        if (ObjectUtils.isEmpty(loginRequest) || StringUtils.isEmpty(loginRequest.getPassword())) {
            throw new CustomException(ResponseCode.AUTH_PASSWORD_NONE);
        }

        //申请令牌
        AuthToken authToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

        //用户访问令牌
        String access_token = authToken.getAccess_token();

        //将访问令牌存储到cookie
        this.saveCookie(access_token);

        // 返回访问令牌
        return new ResponseResult(ResponseCode.AUTH_USER_SUCCESS, access_token);
    }

    /**
     * 用户退出清除令牌
     *
     * @return
     */
    @GetMapping("/userlogout")
    public ResponseResult logout() {

        // 从cookie 中可以用户认证身份令牌
        String auth_id = this.getTokenFormCookie();
        if (StringUtils.isEmpty(auth_id)) {
            return new JwtResponseResult(ResponseCode.AUTH_OPERATION_FAIL, null);
        }

        // 删除redis JWT信息
        authService.deleteToken(auth_id);

        // 清除浏览器cookie
        this.deleteCookie(auth_id);
        return new JwtResponseResult(ResponseCode.AUTH_OPERATION_SUCCESS, null);

    }


    /**
     * 校验用户身份令牌
     *
     * @return JWT令牌
     */
    @GetMapping("/authjwt")
    public JwtResponseResult authJwt() {

        // 从cookie 中可以用户认证身份令牌
        String auth_id = this.getTokenFormCookie();
        if (StringUtils.isEmpty(auth_id)) {
            return new JwtResponseResult(ResponseCode.AUTH_USER_FAIL, null);
        }

        AuthToken userToken = authService.getUserToken(auth_id);
        if (!ObjectUtils.isEmpty(userToken)) {
            String jwt_token = userToken.getJwt_token();
            return new JwtResponseResult(ResponseCode.AUTH_USER_SUCCESS, jwt_token);
        }
        return new JwtResponseResult(ResponseCode.AUTH_USER_FAIL, null);
    }


    //将令牌存储到cookie
    private void saveCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "auth_id", token, cookieMaxAge, false);
    }

    //取出令牌
    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookies = CookieUtil.readCookie(request, "auth_id");
        if (!CollectionUtils.isEmpty(cookies) && !StringUtils.isEmpty(cookies.get("auth_id"))) {
            return cookies.get("auth_id");
        }
        return null;
    }

    //删除cookie
    private void deleteCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "auth_id", token, 0, false);
    }
}
