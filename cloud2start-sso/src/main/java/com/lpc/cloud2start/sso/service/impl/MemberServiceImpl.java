//package com.lpc.cloud2start.sso.service.impl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.lpc.cloud2start.sso.entity.Member;
//import com.lpc.cloud2start.sso.entity.Role;
//import com.lpc.cloud2start.sso.mapper.MemberMapper;
//import com.lpc.cloud2start.sso.service.MemberService;
//
//@Service
//public class MemberServiceImpl implements MemberService {
//	@Autowired
//	private MemberMapper memberMapper;
//
//	@Override
//	public Member selectByUsername(String username) {
//		return memberMapper.selectByUsername(username);
//	}
//
//	@Override
//	public List<Role> selectRoleList(String username) {
//		return memberMapper.selectRoleList(username);
//	}
//
//}