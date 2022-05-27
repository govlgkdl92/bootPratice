package org.zerock.b2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b2.dto.BoardDTO;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.dto.PageRequestDTO;
import org.zerock.b2.dto.PageResponseDTO;
import org.zerock.b2.entity.Board;
import org.zerock.b2.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 가능하면 이쪽에 트랜잭셔널이 붙는게 좋지 않을까요
public class BoardServiceImpl implements BoardService {
    private final ModelMapper modelMapper;

    private final BoardRepository boardRepository;

    //리스트 목록
    @Override
    public PageResponseDTO<BoardListReplyCountDTO> list(PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

/*      BoardDTO 를 사용했을 때... 사용 했던 예전 구문
        modelMapper 는 이제 사용 안해...

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board,BoardDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();*/

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.toList())
                .total((int)result.getTotalElements())
                .build();
    }

    //등록
    @Override
    public Long register(BoardDTO boardDTO) {
        //엔티티를 바꿔줘야 한다! 모델 메퍼를 이용해서~
        Board board = modelMapper.map(boardDTO, Board.class);

        log.info("register ----------------");
        log.info("매핑된 결과................");
        log.info(board);
        log.info("--------------------------");

        //세이브를 하면 똑같은 타입의 애가 반환이 된다.
        Board result = boardRepository.save(board);
        //DB랑 동기화 된 애가 와

        return result.getBno();
    }

    //상세 조회
    @Override
    public BoardDTO readOne(Long bno) {

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        return boardDTO;
    }

    //수정
    @Override
    public void modifyOne(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());

        Board board = result.orElseThrow();

        //제목이랑 내용 수정 -
        //내가 필요한 정보만 수정해야해~
        board.changeTitle(boardDTO.getTitle());
        board.changContent(boardDTO.getContent());

        boardRepository.save(board);
        //기존 정보들은 그대로 있고 (제목,내용)업데이트만 된다.
    }

    //삭제
    @Override
    public void removeOne(Long bno) {
        boardRepository.deleteById(bno);

    }


    //CGLIB - spring 에서 만들어지는... 다이나믹 프록시 동적프록시를 이용해서 만들어진다.




}
