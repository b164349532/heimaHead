package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HelloController {

    @GetMapping("/basic")
    public String hello(Model model) {
        model.addAttribute("name", "zhangsan");
        Student stu = new Student();
        stu.setAge(13);
        stu.setName("lisi");
        model.addAttribute("stu",stu);

        return "01-basic";
    }

}
