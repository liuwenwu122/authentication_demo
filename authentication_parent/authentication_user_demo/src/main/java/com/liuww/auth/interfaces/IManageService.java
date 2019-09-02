package com.liuww.auth.interfaces;

import com.liuww.auth.apilist.RegisterServiceList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date: 2019/9/2
 * @Author: WenWu.Liu
 * @Desc:
 */

@FeignClient(value = RegisterServiceList.MANAGE_SERVICE_DEMO)
public interface IManageService {

    @RequestMapping("/findManagNameByUser")
    String findManagNameByUser();
}
