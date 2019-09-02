package com.liuww.auth.dao;

import com.liuww.auth.domain.auth.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc:
 */
@Mapper
public interface IUserMapper {

    @Select("select * from xc_user where username = #{username}")
    TbUser findUserByUserName(String username);
}
