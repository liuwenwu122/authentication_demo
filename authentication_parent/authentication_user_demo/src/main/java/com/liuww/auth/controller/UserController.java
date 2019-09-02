package com.liuww.auth.controller;


import com.liuww.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserName")
    @PreAuthorize("hasAuthority('xc_sysmanager_user')")// 方法鉴权
    public String getUsername(){
        return "hello：my name is WenWuLiu";
    }

    @GetMapping("/deleteUser") // 方法鉴权
    @PreAuthorize("hasAuthority('xc_sysdelete_user')")
    public String deleteUser(){
        return "delete user success!";
    }

    @RequestMapping("/findManageByUser")
    public String findManageByUser(){
        return userService.findManageByUser();
    }
}
