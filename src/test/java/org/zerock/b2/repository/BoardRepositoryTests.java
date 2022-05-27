package org.zerock.b2.repository;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.entity.Board;
import org.zerock.b2.entity.BoardImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    //첨부파일 insert 테스트
    @Test
    public void testInsertWithImage() {

        for (int i = 0; i < 20; i++) {

            Board board = Board.builder()
                    .title("fileTest.." + i)
                    .content("fileTest" + i)
                    .writer("user" + (i % 10))
                    .build();

            for (int j = 0; j < 2; j++) {
                BoardImage boardImage = BoardImage.builder()
                        .uuid(UUID.randomUUID().toString())
                        .fileName(i+".gif")
                        .img(true)
                        .build();
                board.addImage(boardImage);
            }

            boardRepository.save(board);

            //boardImage 는 board 가 삭제 될 때 같이 삭제 되야해..

            //Board 객체인 board 가 자식 객체인 boardImage 를 바인딩하려 한번에 저장하려고 하는데
            //자식 객체가 아직 데이터베이스에 저장되어있지 않아서 에러가 일어난다.
            //쉽게 말하면 부모객체가 아직 insert 되지 않고 한번에 insert 되려고 하는데
            //update 가 일어나려고 해서 오류가 나는 거다..

            //@OneToMany(fetch = FetchType.LAZY) 에서
            //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
            //이렇게 cascade All 을 추가해준다.
            //그러면 오류가 일어나지 않고 정상적으로 실행됨.

            //, orphanRemoval = true
            //
            
        }
    }


    // 지금부터 n+1 문제 발생! 제일 중요하다!!!!!!!!
    @Transactional
    @Test
    public void testPageImage(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> result = boardRepository.findAll(pageable);

        //검색 조건 없이 목록을 가져올 때,

        result.getContent().forEach(board -> {
            log.info(board);
            log.info(board.getBoardImages());
        });

        //failed to lazily initialize a collection of role:
        //      org.zerock.b2.entity.Board.boardImages, could not initialize proxy - no Session
        // @Transactional 걸어줌

        // 항상 limit 가 잘 걸려 있는 지 확인해야 한다.
        // 실행되는 sql 이 limit 가 잘 걸려있는 지 ...

        // 이렇게 실행하면 board list 를 가져오는 데,
        // 한 게시물 당 select t_board_image 를 하게 된다.
        // 이렇게 되면 쿼리가 너무 많아져서 서버가 다운 된다
        // 이게 바로 n+1 문제

        // Board Entity 에 @ToString(exclude = "boardImage")를 추가해준다.
        // 그렇게 되면 boardImage 를 한 게시물 당 select 하는 문제는 없어진다.
        // 그런데, boardImage 를 통해 섬네일 사진이 필요한데?

        // 그래서 log.info(board.getBoardImages()); 를 추가해줬더니
        // 전처럼 하나씩 image 를 select 하게 된다...

        // 그럼 eager loading? (즉시 로딩)
        // 해결 될까?
        // FetchType.Lazy 를 FetchType.EAGER 로 바꿔줘 보자..
        // EAGER 로딩은 즉시 로딩인데, 그러면 처음부터 조인 할 줄 알았으나
        // 아니네
        // 그럼 어떻게 해야 할까

        // EAGER 로딩은 하나를 가져올 때는 즉시로딩이 된다. 하지만 지금처럼 목록 출력으로
        // 페이징 처리를 할 때는
        // 즉시 로딩이 되지 않는다!

        // 완벽은 아니지만 간단한 해결 방법! 두가지!
        // 1. 게시물에 있는 bno 가 있었다면 첨부파일 테이블에 있는 board_bno 같은 값 2개 안 ord 값은 다르게 되어있다.

        // 2.
    }


    @Transactional
    @Test
    public void testWithQuery() {

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<Board> result = boardRepository.getListOne(pageable);

        result.getContent().forEach(board -> {
            log.info(board);
            log.info(board.getBoardImages());
            log.info("=====================");
        });

    }
    
    //EAGER 로딩일 경우 조회 물이 하나인 경우 즉시로딩이 되는 것을 확인하는 테스트 코드
    @Test
    public void testEager(){
        Long bno = 123L;
        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();
        log.info(board);
        
        // 트랜잭션이 없는대도 잘 실행된다.
        // 목록을 뽑을 때는 Eager Loading 의 의미가 없다.
    }

    // BoardSearchImpl 의 searchAll 테스트
    @Test
    public void testSearchAll(){
        String[] types = new String[]{"t", "c"};
        String keyword = "5";

        Pageable pageable = PageRequest.of(0, 10,  Sort.by("bno").descending());
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        log.info("=============================");
        log.info(result);
        log.info("=============================");
    }


    // search1 테스트
    @Test
    public void testSearch1() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        boardRepository.search1(pageable);
        // 이것만 만들고 실행했는데 오류나네? 컴파일 경로를 못잡아서 그렇다
        // Gradle - other - compileJava 를 한 번 누르고 다시 실행하기
        // 도메인 쪽을 건드리면 compileJava 를 반드시 눌러야 된다고 생각하자.
    }

    // replyCount 테스트
    @Test
    public void testWithReplyCount() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<BoardListReplyCountDTO> result =
                boardRepository.searchWithReplyCount(null, null, pageable);

        /*
                Hibernate:
            select
                board0_.bno as col_0_0_,
                board0_.title as col_1_0_,
                board0_.writer as col_2_0_,
                board0_.reg_date as col_3_0_,
                count(reply1_.rno) as col_4_0_
            from
                t_board board0_
            left outer join
                t_reply reply1_
                    on (
                        reply1_.board_bno=board0_.bno
                    )
            group by
                board0_.bno
            order by
                board0_.bno desc limit ?

          Hibernate:
            select
                count(distinct board0_.bno) as col_0_0_
            from
                t_board board0_
            left outer join
                t_reply reply1_
                    on (
                        reply1_.board_bno=board0_.bno
                    )
        */

        // 카운트 쿼리에 join 할 필요가 없는데 한번에 동작하면 이렇게 나오네!
        // 카운트 쿼리는 낭비스럽게 계산하지 않고, 별도의 쿼리로 만들어야 할 것 같다!


    }


    @Test
    public void testInsert(){
        log.info("--------------boardRepository--------------");
        log.info("------------"+boardRepository+"--------------");
        log.info("---------------------------------------------");

        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("SpringBootTitle"+i)
                    .content("SpringBootContent"+i)
                    .writer("User"+(i%10))
                    .build();
            boardRepository.save(board);
        });
    }

    @Test
    public void testRead(){
        Long bno = 32922L;

        // java.util 에 있는 Optional 이 return 값...
        Optional<Board> result =  boardRepository.findById(bno);
        // NullPointException 안나오게 한번 더 처리를 해주는 것
        // 한번 감싸주고 결과값이 나온다.

        Board board = result.orElseThrow(); //만약에 문제가 있으면 예외를 발생시켜
                // result.get() -> 이런걸로 가져와도 상관은 없다..


        log.info("-----------------------------------------");
        log.info(board);
        log.info("-----------------------------------------");
    }


    @Test
    public void testUpdate(){
      /* Board board = Board.builder()
               .bno(32923)
               .title("수정한 테이블")
               .content("바꿔 볼까?")
               .writer("User22")
               .build();

       //reg_date, update_date 가 문제가 생기기 때문에.. 기존 정보를 다 가지고 온 후 필요한 것만 바꿔서 하는 것이 맞다.

       boardRepository.save(board);*/

        Long bno = 32923L;

        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.get();
        board.changeTitle("Cha Cha change~");
        board.changContent("content update update wow!");

        boardRepository.save(board);

    }


    @Test
    public void testDelete(){
        //없는 번호 삭제 해보자
        Long bno = 32927L;

        boardRepository.deleteById(bno);
        //오류!
    }


    @Test
    public void testPaging1(){
        // Pageable 이 파라미터면 무조건 리턴 타입은 Page<T>

        // import 할 때 주의
        // org.springframework.data.domain.Pageable;          ↓ Sort 는 필요하지 않으면 생략해도 됨
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
                                                                        //  ↑ desc
        //ascending() = asc;
        //descending() = desc;

        // 뒤에 데이터가 더 있다고 생각하면 무조건 count 쿼리도 같이 날라간다.
        // 0, 10 일 때는 limit 부분이 limit ?
        // 1, 10 일 때는 limit 부분이 limit ?, ?


        Page<Board> boards = boardRepository.findAll(pageable);

        // Sort 을 지정하고 싶을 때에는 page, size 값 다음에
        // Sort.Direction direction -> enum 값
        // Sort -> 복잡한 sorting 조건을 써야 할 때 사용

        // page 번호는 0부터 시작한다!!!!

        boards.getTotalElements(); //총 몇개 냐
        boards.getTotalPages(); //총 몇 페이지냐
        boards.getContent(); //리스트로 따로 구해왔었으나... 자동으로 구해준다.
        boards.getNumber();
        boards.hasNext(); //다음페이지 있냐 없냐
        boards.isFirst(); //이거 시작페이지야?
        boards.isLast(); // 이거 마지막 페이지야?

        log.info("===========================================");
        log.info("===========================================");
        log.info("Total: "+boards.getTotalElements());
        log.info("Total Page: "+boards.getTotalPages());
        log.info("Current Page: "+ boards.getNumber());
        log.info("Size: "+boards.getSize());
        log.info("===========================================");
        log.info("===========================================");

        boards.getContent().forEach(board -> log.info(board));

        // 출력할 때 페이지가 0으로 시작하는 것을 제일 고려해야 한다!!
        // 0 페이지가 1페이지임
    }


    @Test
    public void testQueryMethod1(){

        String keyword = "5";

        List<Board> list = boardRepository.findByTitleContaining(keyword);

        log.info(list);
    }

    //pageable 이 파라미터일 때는 return type 이 무조건 page

    //모든 메소드의 마지막을 Pageable 로 해주고 모든 메소드의 마지막의 return type 을 Page 로 하면 페이징 처리도 된다.


    @Test
    public void testQueryMethod2(){
        String keyword = "5";
        Pageable pageable = PageRequest.of(0,5,Sort.by("bno").descending());

        Page<Board> list = boardRepository.findByTitleContaining(keyword, pageable);

        log.info(list);
        
        //검색 조건이 복잡해지면 메소드 이름이 복잡해져서 많이 쓰이지 않는다.
        //↑ 방법1. 쿼리메소드+pageable 조합


        //또 다른 방법2. @Query 어노테이션 , 방법1 보다는 많이 쓰인다.


        //nativeQuery  - 급할 때만 쓰자
        // 객체 지향 설계만으로 데이터베이스
        // 이 nativeQuery 를 사용하면 데이터 베이스의 독립적이라는 이 장점이 사라진다.
        // 추천하지 않지만 개발자들이 많이 쓰는 쿼리문

        // awt
        // 운영체제 마다 쓰레드를 실행하는 방법이 다 다르다.
    }

}
