package com.lpc.cloud2start.sso.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.lpc.cloud2start.sso.entity.Member;
import com.lpc.cloud2start.sso.entity.Role;

@Mapper
public interface MemberMapper {
	// 做登陆，通过用户名获取用户信息
	@Select("select * from shop_member.member where username = #{username} limit 1")
	Member selectByUsername(@Param("username") String username);

	// 通过用户名获取权限列表
	@Select("select * from shop_member.member u,role r,membrole ur where u.username = #{username} and ur.memberId = u.id and ur.roleId = r.id")
	List<Role> selectRoleList(@Param("username") String username);

}