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
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;
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
    /*"""
            Given the URL, please perform the following tasks:
            1. *Generate* the page that is supposed to be on this link, structured like wikipedia and fill it's contents, mainly:short summary, chapters about key points, links to sources, links to topics that are connected to this page with relative lings /wiki/(topic).
            2. *Identify Relevant Links:* As you create the summary, identify key concepts and topics that are extensively discussed on the page. For each of these, find relevant internal links (links to other pages within the same domain) that provide additional context or further information.
            3. *Insert Contextual Links:* Embed these links seamlessly into the summary text where they naturally fit. The anchor text for each link should be informative and indicate the linked page's content, maintaining the coherence and context of the original page.
            4. *Maintain Original Context:* Be careful to preserve the original context of the page. Do not insert links that could mislead or take the reader on a tangent unrelated to the primary focus of the original content. Keep the user on /wiki/(topics) path.
            """;*/
    @Getter
    final static String defaultUserRequest= """
            Generate a Wikipedia-style webpage for the following path : "$$URL$$".
            """;
    /*"""
            AI, I am providing you with the full URL of a webpage: $$URL$$. Based on your knowledge up to 2023, please generate a page that looks that it is supposed to be there. Ensure that the page remains coherent and flows naturally with the inserted hyperlinks.
            """;*/

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
            User u = new User(null, db.getLogin(), null,null,db.getEmail(),false,null,db.getSelectedModel(), db.getPageGenSystemText() ,db.getPageGenUserText());

            if(u.getPageGenSystemText()==null){
                u.setPageGenSystemText(defaultSystemPrompt);
            }
            if(u.getPageGenUserText()==null){
                u.setPageGenUserText(defaultUserRequest);
            }
            if(u.getSelectedModel()==null){
                u.setSelectedModel(defaultModel);
            }
            return new ResponseEntity<>("{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
    }
    @PutMapping("/api/user")
    public ResponseEntity<String> updateInfo(@RequestParam(required = false) String key,@RequestBody User updated) throws JsonProcessingException {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
        Token t = tokens.findByToken(key);
        if(t!=null) {
            User u = t.getUser();

            u.setPageGenUserText(updated.getPageGenUserText());
            u.setPageGenSystemText(updated.getPageGenSystemText());
            u.setSelectedModel(updated.getSelectedModel());

            users.save(u);
            return new ResponseEntity<>("{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
    }
}
