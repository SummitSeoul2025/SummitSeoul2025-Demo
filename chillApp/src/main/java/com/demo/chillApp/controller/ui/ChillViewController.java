package com.demo.chillApp.controller.ui;

import com.demo.chillApp.service.ChillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ChillViewController {

    private final ChillService chillService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("isAnswer", chillService.isAnswer());
        return "index";
    }
} 