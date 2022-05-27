package org.zerock.b2.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b2.dto.BoardDTO;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.dto.PageRequestDTO;
import org.zerock.b2.dto.PageResponseDTO;

@Log4j2
@SpringBootTest
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    //등록 테스트
    @Test
    public void testRegister() {
        BoardDTO boardDTO = BoardDTO.builder()
                .title("제목")
                .content("연습해보는 거야")
                .writer("김이슬")
                .build();
        
        Long bno = boardService.register(boardDTO);

        log.info("-------------------------");
        log.info("bno : "+bno);
    }

    //목록 테스트
    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("5")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

    }



}
