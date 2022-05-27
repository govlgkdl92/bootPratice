package org.zerock.b2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b2.entity.Board;
import org.zerock.b2.repository.search.BoardSearch;

import java.util.List;

// JpaRepository - > CRUD 등 모든 기능을 가지고 있음. paging 도!

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    // 우리는 Board 를 얻어올 텐데
    List<Board> findByTitleContaining(String keyword); // 제목에서 검색

    Page<Board> findByTitleContaining(String keyword, Pageable pageable); // 제목에서 검색 , 페이징 처리도 끝

    //@Query("select b from Board b left join b.boardImages")
    @EntityGraph(attributePaths = {"boardImages"})
    @Query("select b from Board b") // left join BoardImage bi where bi.ord = 0")
    Page<Board> getListOne(Pageable pageable);
            //findBy 어쩌구 하면 sql 문으로 이해하니까 그렇게 이름 짓지 말자.
    // -> 이게 더 나은 쿼리 이지만, 월요일에 엘리먼트 컬렉션으로 바꿀 거다.. 그러면 에러 나지는.. 않음..

    //순수한 코드로 바꿔준 후 Board 의 BoardImages 에
    // @BatchSize(size = 100) 배치 사이즈 코드 붙여주기
    // 이 방법을 쓰면 쿼리를 두번 날리는 단점은 있다...
    // one to Many 방법 / 게시판 리스트에서 모든 첨부파일이 보여야 할 경우가 있을 경우 사용할 수 있다.
    //@Query("select b from Board b")
    //Page<Board> getListOne(Pageable pageable);
    
}
