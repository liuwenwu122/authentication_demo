package com.liuww.auth.filter;

import com.alibaba.fastjson.JSON;
import com.liuww.auth.entity.ResponseCode;
import com.liuww.auth.entity.ResponseResult;
import com.liuww.auth.service.AuthService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */
@Component
public class AuthFileter extends ZuulFilter {


    @Autowired
    private AuthService authService;

    // 过滤器类型
    @Override
    public String filterType() {
        return "pre";
    }

    //执行顺序
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }


    /**
     * 从请求头中获取 Authentication JWT令牌
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        // 获取请求对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        // 获取JWT令牌
        String jwtToken = authService.getJwtFormHeader(request);

        if (StringUtils.isEmpty(jwtToken)) {
            denied(requestContext);
            return null;
        }

        // cookie 身份令牌
        String accessToken = authService.getTokenFormCookie();
        if (StringUtils.isEmpty(accessToken)) {
            denied(requestContext);
            return null;
        }

        // 身份令牌去redis校验(获取用户信息)
        String token = authService.authTokenFormRedis(accessToken);
        if (StringUtils.isEmpty(token)){
            denied(requestContext);
            return null;
        }

        return null;
    }



    private void denied(RequestContext requestContext) {
        requestContext.setSendZuulResponse(false);  // 拒绝访问
        requestContext.setResponseStatusCode(200);  // 设置正常响应
        ResponseResult responseResult = new ResponseResult(ResponseCode.AUTH_USER_FAIL);
        String jsonStr = JSON.toJSONString(responseResult);
        requestContext.getResponse().setContentType("application/json;charset=UTF‐8");
        requestContext.setResponseBody(jsonStr);
    }
}
