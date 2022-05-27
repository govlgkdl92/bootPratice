package org.zerock.b2.service;

import org.zerock.b2.dto.BoardDTO;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.dto.PageRequestDTO;
import org.zerock.b2.dto.PageResponseDTO;

public interface BoardService {
    Long register(BoardDTO boardDTO);//등록
    
    BoardDTO readOne(Long bno); //상세조회

    void modifyOne(BoardDTO boardDTO); //수정
    
    void removeOne(Long bno); //삭제

    PageResponseDTO<BoardListReplyCountDTO> list(PageRequestDTO pageRequestDTO); //보드 목록

}
