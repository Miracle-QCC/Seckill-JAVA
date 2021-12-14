package com.qcj.seckill.controller;/*
 *功能描述
 * @author qcj
 * @param 测试$
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name","秦晨杰");
        return "hello";
    }
}
