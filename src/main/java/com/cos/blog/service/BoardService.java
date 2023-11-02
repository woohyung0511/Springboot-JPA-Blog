package com.cos.blog.service;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import com.cos.blog.repository.UserRepository;

@Service
public class BoardService {

	@Autowired
	public BoardRepository boardRepository;

	@Autowired
	private ReplyRepository replyRepository;

	@Transactional
	public void 글쓰기(Board board, User user) { // title, content
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<Board> 글목록(org.springframework.data.domain.Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Board 글상세보기(int id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("글 상세보기 실패: 아이디를 찾을 수 없습니다"));
	}

	@Transactional
	public void 글삭제하기(int id) {
		boardRepository.deleteById(id);
	}

	@Transactional
	public void 글수정하기(int id, Board requestBoard) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("글 찾기 실패: 아이디를 찾을 수 없습니다"));
		// 영속화 완료

		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		// 해당 함수로 종료 시(Service가 종료될 때) 트랜잭션이 종료됩니다. 이때 더티 체킹 - 자동 업데이트 됨 db flush
	}

	@Transactional
	public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) {
		int result = replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
		System.out.println("BoardService : "+result);
	}
	
	@Transactional
	public void 댓글삭제(int replyId) {
		replyRepository.deleteById(replyId);
	}

}
