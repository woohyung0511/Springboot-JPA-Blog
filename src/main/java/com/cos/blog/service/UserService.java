package com.cos.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

//스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌.IoC를 해준다.
@Service
public class UserService {

	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional(readOnly = true)
	public User 회원찾기(String username) {
		User user = userRepository.findByUsername(username).orElseGet(()->{
			return new User();
		});
		
		return user;
	}
	
	@Transactional
	public void 회원가입(User user) {
		String rawPassword = user.getPassword(); //1234 원문
		String encPassword = encoder.encode(rawPassword); //해쉬
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
	}
	
	@Transactional
	public void 회원수정(User user) {
	    User persistence = userRepository.findById(user.getId())
	            .orElseThrow(() -> new IllegalArgumentException("회원 찾기 실패"));
	    
	    // Validate 체크 => oauth에 값이 없으면 수정 가능
	    if(persistence.getOauth() == null || persistence.getOauth().equals("")) {
		    String rawPassword = user.getPassword();
		    String encPassword = encoder.encode(rawPassword);
		    persistence.setPassword(encPassword);
		    persistence.setEmail(user.getEmail());
	    }
	    
	    // 회원 수정 함수 종료 = 서비스 종료 = 트랜잭션 종료 = commit이 자동으로 된다.
	    // 영속화된 persistance 객체의 변화가 감지되면 더티체킹이 되어 update문을 날려준다.
	}
}
