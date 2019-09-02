package com.liuww.auth.dao;

import com.liuww.auth.domain.auth.TbPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @Date: 2019/9/1
 * @Author: WenWu.Liu
 * @Desc:
 */
@Mapper
public interface IPermissionMapper {

    @Select("select p.id,p.code,p.p_id pId,p.permi_name permiName,p.url,p.is_menu isMenu,p.level,p.sort,p.status,p.icon,p.create_time createTime,p.update_time updateTime \n" +
            "from xc_user as u join xc_user_role as ur on u.id = ur.user_id join `xc_role` as r on\n" +
            "ur.role_id = r.id join xc_role_permi as rp on r.id = rp.role_id join xc_permission as p\n" +
            "on rp.permi_id = p.id where u.id = #{userId}")
    List<TbPermission> findPermissionByUserId(String userId);
}
