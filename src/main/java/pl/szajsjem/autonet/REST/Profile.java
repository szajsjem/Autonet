package pl.szajsjem.autonet.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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
            Given the full URL, please perform the following tasks:
            1. *Extract and Summarize Content:* Retrieve the content from the specified URL and create a concise summary that captures the main points and purpose of the page. Ensure the summary is structured with a clear introduction, body, and conclusion reflecting the original content's organization.
            2. *Identify Relevant Links:* As you create the summary, identify key concepts and topics that are extensively discussed on the page. For each of these, find relevant internal links (links to other pages within the same domain) that provide additional context or further information.
            3. *Insert Contextual Links:* Embed these links seamlessly into the summary text where they naturally fit. The anchor text for each link should be informative and indicate the linked page's content, maintaining the coherence and context of the original page.
            4. *Maintain Original Context:* Be careful to preserve the original context of the page. Do not insert links that could mislead or take the reader on a tangent unrelated to the primary focus of the original content.
            Please provide the summary along with the contextually embedded links in html format.
            """;
    @Getter
    final static String defaultUserRequest= """
            AI, I am providing you with the full URL of a webpage: $$URL$$. Based on your knowledge up to 2023, please generate a concise summary of the main content of this page, not exceeding 300 words. The summary should capture the key points and themes of the original content. Additionally, incorporate up to five relevant hyperlinks within the summary that enhance the reader's understanding without detracting from the core context of the page. These can be either important internal links to other sections of the same website or external links to authoritative sources that offer additional information on the topics covered. Ensure that the summary remains coherent and flows naturally with the inserted hyperlinks.
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
    String info(@RequestParam(required = false) String key) throws JsonProcessingException {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return "{\"ok\":false}";
        Token t = tokens.findByToken(key);
        if(t!=null) {
            User u = t.getUser();
            if(u.getPageGenSystemText()==null){
                u.setPageGenSystemText(defaultSystemPrompt);
            }
            if(u.getPageGenUserText()==null){
                u.setPageGenUserText(defaultUserRequest);
            }
            return "{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}";
        }
        return "{\"ok\":false}";
    }
    @PutMapping("/api/user")
    String updateInfo(@RequestParam(required = false) String key,@RequestBody User updated) throws JsonProcessingException {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return "{\"ok\":false}";
        Token t = tokens.findByToken(key);
        if(t!=null) {
            User u = t.getUser();

            u.setPageGenUserText(updated.getPageGenUserText());
            u.setPageGenSystemText(updated.getPageGenSystemText());

            users.save(u);
            return "{\"ok\":true," +
                    "\"data\":" + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(u) + "}";
        }
        return "{\"ok\":false}";
    }
}
