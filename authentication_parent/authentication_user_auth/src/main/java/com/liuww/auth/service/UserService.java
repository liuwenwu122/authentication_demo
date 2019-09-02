package com.liuww.auth.service;

import com.liuww.auth.dao.IPermissionMapper;
import com.liuww.auth.dao.IUserMapper;
import com.liuww.auth.domain.auth.TbPermission;
import com.liuww.auth.domain.auth.TbUser;
import com.liuww.auth.domain.auth.TbUserExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc:
 */
@Service
public class UserService {

    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private IPermissionMapper permissionMapper;

    public TbUser findUserByUserName(String username) {
        return userMapper.findUserByUserName(username);
    }


    public TbUserExt getUserExt(String username) {

        TbUser user = this.findUserByUserName(username);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }

        String userId = user.getId();

        TbUserExt userExt = new TbUserExt();
        BeanUtils.copyProperties(user,userExt);

        List<TbPermission> permissions = permissionMapper.findPermissionByUserId(userId);
        if (!CollectionUtils.isEmpty(permissions)){
            userExt.setPermissions(permissions);
        }
        return userExt;
    }
}
