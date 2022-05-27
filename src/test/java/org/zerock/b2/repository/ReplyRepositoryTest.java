package org.zerock.b2.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b2.entity.Board;
import org.zerock.b2.entity.Reply;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTest {

    @Autowired
    ReplyRepository replyRepository;

    @Test
    public void testInsert(){
        //단방향일 때는 pk를 기준으로 개발..

        Board board = Board.builder().bno(103L).build();

        for (int i = 1; i < 101; i++) {
            Reply reply = Reply.builder()
                    .board(board)
                    .replyText(i+"번의 댓글")
                    .replier(i+"번 손님")
                    .build();

            replyRepository.save(reply);
        }
    }


    //@Transactional - 트랜잭션 어노테이션 에러 안난다. select 두번 reply, board
    @Test
    public void testRead(){
        // 에러 원인 : board 가져올 때 database 연결을 해야된다는 건데, 사실 board 의 내용은 필요하지 않아...
        // 그렇다면 ToString 을 할 때 board 를 빼고 하면 되겠네!
        // ToString 에 exclude="board"를 걸고 다시 실행하면
        // 성공!

        Long rno = 99L;

        Optional<Reply> result = replyRepository.findById(rno);
        Reply reply = result.orElseThrow();

        log.info(reply);
    }


    @Test
    public void getList(){
        Long bno = 100L;
        Pageable pageable =                                     //순차적으로 asc
                PageRequest.of(0,10, Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        
        // 실행한 쿼리 문이 인덱스를 타는 지 확인해보자
    }

}
