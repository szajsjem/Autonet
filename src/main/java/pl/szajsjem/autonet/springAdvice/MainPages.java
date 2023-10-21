package pl.szajsjem.autonet.springAdvice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPages {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
