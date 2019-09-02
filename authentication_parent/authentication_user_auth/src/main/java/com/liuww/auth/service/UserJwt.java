package com.liuww.auth.service;

import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc:
 */

@Data
@ToString
public class UserJwt extends User {

    private String id;
    private String name;
    private String userpic;
    private String utype;

    public UserJwt(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }


}
