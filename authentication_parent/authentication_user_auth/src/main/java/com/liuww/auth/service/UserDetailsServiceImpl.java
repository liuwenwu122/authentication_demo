package com.liuww.auth.service;


import com.liuww.auth.domain.auth.TbPermission;
import com.liuww.auth.domain.auth.TbUserExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc: spring security 需要实现 UserDetailsService
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //取出当前登录身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //没有认证统一采用httpBasic认证，httpBasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if (clientDetails != null) {
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        // **********访问用户信息，数据查询***********
        TbUserExt userext = userService.getUserExt(username);

        if (userext == null) {
            return null;
        }

        //取出正确密码（hash值）
        String password = userext.getPassword();

        //用户权限，
        List<TbPermission> permissions = userext.getPermissions();
        List<String> user_permission = new ArrayList<>();
        permissions.forEach(item -> user_permission.add(item.getCode()));
        String user_permission_string = StringUtils.join(user_permission.toArray(), ",");

        // 这个username指的是？
        UserJwt userDetails = new UserJwt(username, password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
        userDetails.setId(userext.getId());
        userDetails.setUtype(userext.getUtype());           //用户类型
        userDetails.setName(userext.getName());             //用户名称
        userDetails.setUserpic(userext.getUserpic());       //用户头像

        return userDetails;
    }
}
