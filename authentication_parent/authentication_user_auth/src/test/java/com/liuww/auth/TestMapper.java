package com.liuww.auth;

import com.liuww.auth.dao.IPermissionMapper;
import com.liuww.auth.dao.IUserMapper;
import com.liuww.auth.domain.auth.TbPermission;
import com.liuww.auth.domain.auth.TbUser;
import com.liuww.auth.domain.auth.TbUserExt;
import com.liuww.auth.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMapper {


    @Autowired
    private IUserMapper IUserMapper;

    @Autowired
    private IPermissionMapper permissionMapper;

    @Autowired
    private UserService userService;

    //创建jwt令牌
    @Test
    public void testMapper() {

        String username = "admin";
        TbUser user = IUserMapper.findUserByUserName(username);

        System.out.println(user);

    }


    @Test
    public void testMapper2(){
        String username = "admin";
        TbUser user = IUserMapper.findUserByUserName(username);

        String userId = user.getId();

        TbUserExt userExt = new TbUserExt();
        BeanUtils.copyProperties(user,userExt);
        List<TbPermission> permissions = permissionMapper.findPermissionByUserId(userId);
        if (CollectionUtils.isEmpty(permissions)){
            userExt.setPermissions(permissions);
        }
        System.out.println(userExt);
    }

    @Test
    public void test3(){
        String username = "jack";
        TbUserExt userExt = userService.getUserExt(username);
        System.out.println(userExt);
    }

}
