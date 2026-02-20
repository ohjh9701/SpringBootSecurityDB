package com.zeus.mapper;

import com.zeus.domain.Member;

public interface MemberMapper {
	
	public Member read(String username) throws Exception;
}
