package pl.szajsjem.autonet.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

@RestController
public class Login {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;

    @PostMapping("/api/user/login")
    String login(@RequestBody Map<String,String> map) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User u = null;
        if(!map.containsKey("password"))return "{\"ok\":\"false\",\"data\":{\"message\":\"missing password\"}}";
        if (map.containsKey("login")) {
            u = users.findByLogin(map.get("login"));
            if (u == null)
                u = users.findByEmail(map.get("login"));
        }
        else if (map.containsKey("email")) {
            u = users.findByEmail(map.get("email"));
        }
        if (u == null) return "{\"ok\":\"false\",\"data\":{\"message\":\"missing login or email\"}}";
        if(!u.getPassword().equals(passhash(u.getSalt(),map.get("password"))))return "{\"ok\":\"false\",\"data\":{\"message\":\"invalid password\"}}";
        String token = RandomString(40);
        while (tokens.findByToken(token) != null) {
            token = RandomString(40);
        }
        tokens.save(new Token(token, u));
        return "{\"ok\":\"true\",\"data\":{\"token\":\""+token+"\"}}";
    }
    @DeleteMapping("/api/user/logout")
    void forceLogout(@RequestParam String key) {
        if(tokens.findByToken(key)!=null)
            tokens.removeByToken(key);
    }

    @PostMapping("/api/user/register")
    String register(@RequestBody Map<String,String> map) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(map.isEmpty())return "fu";
        if(!map.containsKey("login"))return "dodaj login w wywołaniu";
        if(!map.containsKey("password"))return "dodaj haslo w wywołaniu";
        if(!map.containsKey("email"))return "dodaj email w wywołaniu";
        if(users.findByLogin(map.get("login"))!=null)return "login jest już zajęty";
        if(users.findByEmail(map.get("email"))!=null)return "email jest już wykorzystany";
        if(map.get("password").length()<3)return "hasło jest za krótkie";
        if(map.get("login").length()<3)return "login jest za krótki";
        if(map.get("email").length()<3)return "email jest za krótki";
        if(!map.get("email").contains("@"))return "email bez małpy?";
        if(!map.get("email").contains("."))return "email bez kropki?";
        byte[] salt=new byte[16];
        for(int i=0;i<16;i++)
            salt[i]= ((byte) (Math.random()*255));
        users.save(new User(0L,map.get("login"),passhash(salt,map.get("password")),salt,map.get("email"),false,new HashSet<>()));
        return "{\"ok\":\"true\"}";
    }


    private String RandomString(final int i) {
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
