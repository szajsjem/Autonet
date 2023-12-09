package pl.szajsjem.autonet.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.szajsjem.autonet.DB.entity.GenInstructions;
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;
import pl.szajsjem.autonet.DB.jpa.GenInstructionsRepository;
import pl.szajsjem.autonet.DB.jpa.TokenRepository;
import pl.szajsjem.autonet.DB.jpa.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.random.RandomGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@RestController
public class Profile {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;
    @Autowired
    private GenInstructionsRepository genInstructionsRepository;

    @Getter
    final static String defaultModel = "GPT3.5";
    @Getter
    final static String defaultSystemPrompt= """
                    You are an HTTP server powered by GPT,
                    designed to generate web pages that mimic the appearance and content structure of Wikipedia.
                    Your goal is to respond to user requests for information by generating web pages with relevant content.
                    Your responses should be coherent, factually accurate, and presented in a style similar to Wikipedia articles.
                    Feel free to use hyperlinks, sections, and references to enhance the authenticity of the generated content.
                    Keep the links relative and with this structure "/wiki/(topic)/(subtopic)";
            """;
    @Getter
    final static String defaultUserRequest= """
            Generate a Wikipedia-style webpage for the following path : "$$URL$$".
            """;

    @GetMapping("/api/Defaults")
    String defaultStrings(){
        return """
                {"ok":true,
                "data":{
                    "model":\""""+defaultModel+"""
                    ",
                    "system":\""""+defaultSystemPrompt+"""
                    ",
                    "user":\""""+defaultUserRequest+"""
                    "
                }}
                """;
    }

    @GetMapping("/api/user")
    public ResponseEntity<String> info(@RequestParam(required = false) String key) throws JsonProcessingException {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
        Token t = tokens.findByToken(key);
        if(t!=null) {
            User db = t.getUser();
            User u = new User(null, db.getLogin(), null,null,db.getEmail(),false,null,new GenInstructions());

            GenInstructions g = db.getGenerationInstructions();
            if (g != null) {
                if (g.getPageGenSystemText() == null) {
                    u.getGenerationInstructions().setPageGenSystemText(defaultSystemPrompt);
                }
                else{
                    u.getGenerationInstructions().setPageGenSystemText(g.getPageGenSystemText());
                }
                if (g.getPageGenUserText() == null) {
                    u.getGenerationInstructions().setPageGenUserText(defaultUserRequest);
                }
                else{
                    u.getGenerationInstructions().setPageGenUserText(g.getPageGenUserText());
                }
                if (g.getSelectedModel() == null) {
                    u.getGenerationInstructions().setSelectedModel(defaultModel);
                }
                else{
                    u.getGenerationInstructions().setSelectedModel(g.getSelectedModel());
                }
            }
            else{
                u.getGenerationInstructions().setPageGenSystemText(defaultSystemPrompt);
                u.getGenerationInstructions().setPageGenUserText(defaultUserRequest);
                u.getGenerationInstructions().setSelectedModel(defaultModel);
            }
            return new ResponseEntity<>("{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
    }
    @PutMapping("/api/user")
    public ResponseEntity<String> updateInfo(@RequestParam(required = false) String key,@RequestBody GenInstructions updated) throws JsonProcessingException {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
        Token t = tokens.findByToken(key);
        if(t!=null) {
            User u = t.getUser();
            GenInstructions g = u.getGenerationInstructions();
            if(g!=null) {
                g.setPageGenUserText(updated.getPageGenUserText());
                g.setPageGenSystemText(updated.getPageGenSystemText());
                g.setSelectedModel(updated.getSelectedModel());
            }
            else{
                GenInstructions n = new GenInstructions(0L,updated.getSelectedModel(),updated.getPageGenSystemText(),updated.getPageGenUserText());
                g = genInstructionsRepository.save(n);
                u.setGenerationInstructions(g);
            }
            users.save(u);
            return new ResponseEntity<>("{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
    }
}
