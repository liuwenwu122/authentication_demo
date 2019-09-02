package com.liuww.auth.controller;

import com.liuww.api.manage.ManageServiceApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */

@RestController
@RequestMapping("/manage")
public class ManageController implements ManageServiceApi {


    @Override
    @RequestMapping("/findManagNameByUser")
    public String findManagNameByUser() {

        return "SUCCESS";
    }
}
