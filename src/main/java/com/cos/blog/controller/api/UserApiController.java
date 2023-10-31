package com.cos.blog.controller.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.dto.ResponseDto;
import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserApiController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private BCryptPasswordEncoder encode;
	
	@PostMapping("/auth/joinProc")
	public ResponseDto<Integer> save(@RequestBody User user) { //username,password,email
		System.out.println("UserApiController: save 호출됨");
		userService.회원가입(user);
		return new ResponseDto<Integer>(HttpStatus.OK.value(),1); //자바오브젝트를 Json으로 변환해서 리턴(Jackson)
	}
	
	@PutMapping("/user")
	public ResponseDto<Integer> update(@RequestBody User user){ //key = value, x-www-form-urlencoded
		userService.회원수정(user);
		// 여기서는 트랜잭션이 종료되기 때문에 DB에 값은 변경이 됨.
		// 하지만 세션값은 남아있어서 직접 변경 해 줘야함.
	    //세션 등록
	    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	    SecurityContextHolder.getContext().setAuthentication(authentication);

		return new ResponseDto<Integer>(HttpStatus.OK.value(),1);
	}
}
