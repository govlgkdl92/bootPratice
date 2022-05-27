package org.zerock.b2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b2.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    //JPQL - 테이블을 기준으로 하는 것이 아니라, 엔티티를 기준으로 해야한다.
            // 예를 들어 t_reply 가 아닌 Reply
    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

}
