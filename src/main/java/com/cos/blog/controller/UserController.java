package com.cos.blog.controller;

import java.net.HttpURLConnection;
import java.net.http.HttpHeaders;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OauthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Value;

//인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용
//그냥 주소가 / 이면 index.jsp 허용
//static 이하에 있는 /js/**, /css/**, /image/**

@Controller
public class UserController {

	@org.springframework.beans.factory.annotation.Value("${cos.key}")
	private String coskey;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@GetMapping("/auth/joinForm")
	public String joinForm() {

		return "user/joinForm";
	}

	@GetMapping("/auth/loginForm")
	public String loginForm() {

		return "user/loginForm";
	}

	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { // Data를 리턴해주는 컨트롤러 함수

		// POST 방식으로 key = value 데이터를 요청 (카카오쪽으로)
		// Retrofit2
		// OKhttp
		// RestTemplate

		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "7285252a3fd45dea6b968cd75f79d5cf");
		params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
		params.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenrequest = new HttpEntity<>(params, headers);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenrequest, String.class);

		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		OauthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OauthToken.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RestTemplate rt2 = new RestTemplate();

		// HttpHeader 오브젝트 생성
		org.springframework.http.HttpHeaders headers2 = new org.springframework.http.HttpHeaders();
		headers2.add("Authorization", "Bearer" + oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfilerequest2 = new HttpEntity<>(headers2);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfilerequest2, String.class);

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// User 오브젝트 : username,password,email
		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account());

		System.out.println("블로그 서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
		System.out.println("블로그 서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());
		//UUID -> 중복되지 않는 어떤 트정 값을 만들어내는 알고리즘
		System.out.println("블로그 서버 패스워드 : " + coskey);

		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
				.password(coskey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();

		// 가입자 혹은 비가입자 체크 해서 처리
		User originUser = userService.회원찾기(kakaoUser.getUsername());

		if (originUser.getUsername() == null) {
			System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다.");
			userService.회원가입(kakaoUser);
		}

		System.out.println("자동 로그인을 진행합니다.");
		// 로그인 처리
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), coskey));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/";
	}

	@GetMapping("/user/updateForm")
	public String updateForm() {
		return "user/updateForm";
	}
}
