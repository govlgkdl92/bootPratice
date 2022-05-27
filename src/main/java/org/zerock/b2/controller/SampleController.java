package org.zerock.b2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/sample")
public class SampleController {

    @GetMapping("/hello")
    public void hello(Model model){
        model.addAttribute("msg", "반갑습니다.");

        log.info("===========hello==============");

    }//end hello

    @GetMapping("/ex1")
    public void ex1(Model model){

        List<String> list = Arrays.asList("AAA","BBB","CCC","DDD","EEE");
        List<String> pet = Arrays.asList("강아지", "고양이", "앵무새", "햄스터", "토끼");
        

        model.addAttribute("list", list);
        model.addAttribute("pet", pet);
    }//end ex1



    @GetMapping("/ex2")
    public void ex2(Model model, RedirectAttributes rttr){
        Map<String, String> map =
                Map.of("key1", "value1", "key2", "value2", "key3", "value3");
        // Map.of 로 만들어진 애들은 불변, 내용물 변경 불가함

        model.addAttribute("result", map);

        //리다이렉트 할 때 써야 함
        //rttr.addFlashAttribute("result", map);

        List<String> list =
                IntStream.rangeClosed(10, 20).mapToObj(value -> "DATA=="+value) //문자열로 바꿀 때
                   .collect(Collectors.toList());

        model.addAttribute("list", list);
    }//end ex2

    @GetMapping("/ex3")
    public void ex3(Model model){

        List<String> list = Arrays.asList("AAA","BBB","CCC","DDD","EEE");
        List<String> pet = Arrays.asList("강아지", "고양이", "앵무새", "햄스터", "토끼");


        model.addAttribute("list", list);
        model.addAttribute("pet", pet);
    }//end ex1
}
