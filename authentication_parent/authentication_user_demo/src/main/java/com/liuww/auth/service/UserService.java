package com.liuww.auth.service;

import com.liuww.auth.interfaces.IManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */
@Service
public class UserService {

    @Autowired
    private IManageService manageService;

    public String findManageByUser(){

        return manageService.findManagNameByUser();
    }
}
