package pl.szajsjem.autonet.springAdvice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPages {
    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    @GetMapping("/topic")
    public String topic() {
        return "topic";
    }
    @GetMapping("/editor")
    public String rditor() {
        return "editor";
    }
}
