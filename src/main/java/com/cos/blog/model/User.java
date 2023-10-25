package com.cos.blog.model;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // User 클래스가 MySQL에 테이블이 생성이 된다.
public class User {

	@Id //Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
	private int id; // 시퀀스,auto_increment

	@Column(nullable = false, length = 30)
	private String username; //아이디
	
	@Column(nullable = false, length = 100) //123456  => 해쉬 (비밀번호 암호화)
	private String password;
	
	@Column(nullable = false, length = 50)
	private String email;
	
	@ColumnDefault("'user'")
	private String role; //Enum을 쓰는게 좋다. //admin, user, manager
	
	@CreationTimestamp //시간이 자동입력
	private Timestamp createDate;
}
