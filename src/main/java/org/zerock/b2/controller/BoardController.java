package org.zerock.b2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b2.dto.BoardDTO;
import org.zerock.b2.dto.BoardListReplyCountDTO;
import org.zerock.b2.dto.PageRequestDTO;
import org.zerock.b2.dto.PageResponseDTO;
import org.zerock.b2.service.BoardService;

import javax.validation.Valid;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    //의존성 주입 자동으로 처리되니 파이널로만 선언하는 거 기억하자

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);
    }


    @GetMapping("/register")
    public void registerGET() {

    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO
                               // validation 진행 할거야
                             , BindingResult bindingResult
                             , RedirectAttributes rttr) {

        log.info("");

        if(bindingResult.hasErrors()){
            log.info("has errors.........");
            rttr.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }

        long bno = boardService.register(boardDTO);

        rttr.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }

    @GetMapping("/read")
    public void read(long bno, PageRequestDTO pageRequestDTO, Model model){
        log.info("read: "+bno);
        log.info("read: "+pageRequestDTO);

        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("boardDTO[readOne]: "+boardDTO);

        model.addAttribute("dto", boardDTO);
        model.addAttribute("list",pageRequestDTO);
    }



}
