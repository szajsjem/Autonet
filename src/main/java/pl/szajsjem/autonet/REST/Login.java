package pl.szajsjem.autonet.REST;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import pl.szajsjem.autonet.DB.DTO.LoginRequest;
import pl.szajsjem.autonet.DB.DTO.RestResponse;
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
public class Login {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;

    @PostMapping("/api/user/login")
    RestResponse login(@RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User u = null;
        if(loginRequest.password!=null)return new RestResponse(false,null,"Missing password");
        if (loginRequest.login!=null) {
            u = users.findByLogin(loginRequest.login);
            if (u == null)
                u = users.findByEmail(loginRequest.login);
        }
        else if (loginRequest.email!=null) {
            u = users.findByEmail(loginRequest.email);
        }
        if (u == null) return new RestResponse(false,null,"Wrong username or password");
        if(!u.getPassword().equals(passhash(u.getSalt(),loginRequest.password)))return new RestResponse(false,null,"Wrong password");;
        String token = randomString(40);
        while (tokens.findByToken(token) != null) {
            token = randomString(40);
        }
        tokens.save(new Token(0L,token, u));

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        session.setAttribute("token", token);

        return new RestResponse(true,token,null);
    }
    @Transactional
    @DeleteMapping("/api/user/logout")
    public void logout(@RequestParam(required = false) String key) {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return;
        if(tokens.findByToken(key)!=null)
            tokens.removeByToken(key);
    }

    @PostMapping("/api/user/register")
    String register(@RequestBody Map<String,String> map) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(map.isEmpty())return "{\"ok\":false,\"data\":{\"message\":\"Hello there\"}}";
        if(!map.containsKey("login"))return "{\"ok\":false,\"data\":{\"message\":\"missing username\"}}";
        if(!map.containsKey("password"))return "{\"ok\":false,\"data\":{\"message\":\"missing password\"}}";
        if(!map.containsKey("email"))return "{\"ok\":false,\"data\":{\"message\":\"missing email\"}}";
        if(users.findByLogin(map.get("login"))!=null)return "{\"ok\":false,\"data\":{\"message\":\"username is already taken\"}}";
        if(users.findByEmail(map.get("email"))!=null)return "{\"ok\":false,\"data\":{\"message\":\"email is already in use\"}}";
        if(map.get("password").length()<3)return "{\"ok\":false,\"data\":{\"message\":\"password is too short\"}}";
        if(map.get("login").length()<3)return "{\"ok\":false,\"data\":{\"message\":\"username is too short\"}}";
        if(map.get("email").length()<3)return "{\"ok\":false,\"data\":{\"message\":\"email is too short\"}}";
        if(!map.get("email").contains("@"))return "{\"ok\":false,\"data\":{\"message\":\"invalid email\"}}";
        if(!map.get("email").contains("."))return "{\"ok\":false,\"data\":{\"message\":\"invalid email\"}}";
        byte[] salt=new byte[16];
        for(int i=0;i<16;i++)
            salt[i]= ((byte) (Math.random()*256));
        users.save(new User(0L,map.get("login"),passhash(salt,map.get("password")),salt,map.get("email"),false,new HashSet<>(),null));
        return "{\"ok\":true}";
    }
    @GetMapping("/api/user/testlogin")
    String check(@RequestParam(required = false) String key) {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)return "{\"ok\":false}";
        if(tokens.findByToken(key)!=null)return "{\"ok\":true}";
        return "{\"ok\":false}";
    }


    private String randomString(final int i) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(i);
        for (int j = 0; j < i; j++) {
            sb.append(AB.charAt((int)(Math.random() * AB.length())));
        }
        return sb.toString();
    }
    private String passhash(byte[] salt, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 600000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Arrays.toString(hash);
    }
}
