package org.zerock.b2.repository.search;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.entity.Board;

public interface BoardSearch {

    //테스트 할 수 있는 메소드 일단 돌려보자
    void search1(Pageable pageable);

    //1. 인터페이스 정의 
    //2. 인터페이스 이름이 붙는 클래스를 선언
    //3. implements 2번 클래스
    //4. extends QuerydslRepositorySupport -> 생성자가 있기 때문에 에러남
    //5. 생성자를 생성 후 고치기
    // -> public BoardSearchImpl() {
    //        super(Board.class);
    //    }
    //6. BoardRepository 에 가서 extends 뒤에 ,BoardSearch 추가하기

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,
                                                      String keyword,
                                                      Pageable pageable);

}
