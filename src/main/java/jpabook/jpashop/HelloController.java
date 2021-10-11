package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        // model에 data 실어서 view에 넘길 수 있음
        model.addAttribute("data", "hello!!!");
        return "hello";
        // return은 화면 이름으로 resources 아래 templates에 존재하는 view 네임이 hello.html(springboot thymeleaf viewName 매핑)
    }
}
