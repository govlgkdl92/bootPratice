package org.zerock.b2.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.entity.Board;
import org.zerock.b2.entity.QBoard;
import org.zerock.b2.entity.QReply;

import java.util.List;
import java.util.stream.Collectors;

// override alt+shift+enter
@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public void search1(Pageable pageable) {
        log.info("search1------------------");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));

        //query.select 를 하면 Tuple 이 나옴
        JPQLQuery<Tuple> tupleQuery = query.select(board.bno, board.title, board.writer, reply.count() );
        tupleQuery.groupBy(board);

        List<Tuple> tupleList = tupleQuery.fetch();

        //페이징
        getQuerydsl().applyPagination(pageable, tupleQuery);

        long totalCount = tupleQuery.fetchCount();

        //tuple - data base 의 한 row 를 말한다.
        
        List<Object[]> arr =
                tupleList.stream().map(tuple -> tuple.toArray())
                        .collect(Collectors.toList());
        //tuple 은 가져오지 않는다. object 로 바꾼 후 프로덕스의 bean 을 이용하여 dto 로 바꿔서 사용하기
        
        //distinct - 중복 제거

        // query.select - 원하는 것 뽑아내기
        // query.where - ㅇㅇ가 true인 애들..
        // query.fetchCount(); - 자동으로 카운트는 안해주니 직접 가져와야 함

    } // End search1

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        //types 가 널이 아닐 때만 체킹하기
        if(types != null){
            // com.querydsl.core. 로 import 하기
            BooleanBuilder booleanBuilder = new BooleanBuilder();
                    
            for(String type:types){
                if(type.equals("t")){
                    booleanBuilder.or(board.title.contains(keyword));
                }else if(type.equals("c")){
                    booleanBuilder.or(board.content.contains(keyword));
                }else if(type.equals("w")){
                    booleanBuilder.or(board.writer.contains(keyword));
                }
            }//end for
            query.where(booleanBuilder);
        }//end if
        query.where(board.bno.gt(0));
                    // board.bno 가 0 보다 크다는 조건식

        //실제로 데이터를 가져오는 게 fetch
        //실제 데이터 수를 가져오는 게 fetchCount

        //페이징 처리
        getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();
           // 데이터가 많을 수 있기 때문에 int 사용 안하고 보통 long 을 사용한다.

        // 검색 개발 끝!
        return new PageImpl<>(list, pageable, count);
    }// End searchAll


    //
    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board); //그냥 board 로 group by를 하면 board.bno 로 됨
        
        JPQLQuery<BoardListReplyCountDTO> dtojpqlQuery =
                query.select(Projections.bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount")));
        //Projections.bean DTO.class 를 지정해준다.

        this.getQuerydsl().applyPagination(pageable, dtojpqlQuery);

        List<BoardListReplyCountDTO> list = dtojpqlQuery.fetch(); //fetch 해주면 데이터가 나옴...
        
        long totalCount = dtojpqlQuery.fetchCount(); // 커스텀마이징 할 필요가 있음
                                                    // 쓸데없는 outer join이 함께 들어가기 때문에

        return new PageImpl<>(list, pageable, totalCount);

    }// End searchWithReplyCount




}//End Class














