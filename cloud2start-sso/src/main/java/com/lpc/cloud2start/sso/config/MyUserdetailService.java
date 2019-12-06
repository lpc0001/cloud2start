package com.lpc.cloud2start.sso.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lpc.cloud2start.sso.entity.Member;
import com.lpc.cloud2start.sso.entity.Role;
import com.lpc.cloud2start.sso.mapper.MemberMapper;

@Service
public class MyUserdetailService implements UserDetailsService {

	@Autowired
	private MemberMapper memberMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberMapper.selectByUsername(username);
		if (member == null) {
			System.out.println("用户不存在" + username);
			throw new UsernameNotFoundException("用户不存在" + username);
		}
		List<Role> selectRoleList = memberMapper.selectRoleList(username);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role role : selectRoleList) {
			GrantedAuthority ga = new SimpleGrantedAuthority(role.getCode());
			authorities.add(ga);
		}
		try {
			User user = new User(username, member.getPassword(), authorities);
			return user;
		} catch (Exception e) {
			System.out.println("验证不成功，密码出错");
			e.printStackTrace();
		}
		return null;
	}

}
