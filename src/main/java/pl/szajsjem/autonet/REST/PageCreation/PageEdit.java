package pl.szajsjem.autonet.REST.PageCreation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.szajsjem.autonet.DB.Cache;
import pl.szajsjem.autonet.DB.entity.GenInstructions;
import pl.szajsjem.autonet.DB.entity.Page;
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;
import pl.szajsjem.autonet.DB.jpa.PageRepository;
import pl.szajsjem.autonet.DB.jpa.TokenRepository;
import pl.szajsjem.autonet.DB.jpa.UserRepository;

@Controller
public class PageEdit {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;
    @Autowired
    PageRepository pageRepository;
    @GetMapping("/edit/**")
    public ResponseEntity<String> editWikiPage(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        String p = path.replaceAll("\\/edit\\/","\\/wiki\\/");
        Page page = pageRepository.findByUrl(p);
        if(page != null) {
            if (page.isGenerated()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", "/editor?page=" + p);
                return new ResponseEntity<String>(headers, HttpStatus.FOUND);
            }
            if(page.getErrorMessage()!=null)
                return new ResponseEntity<String>(page.getErrorMessage(), HttpStatus.PARTIAL_CONTENT);
        }
        return new ResponseEntity<String>("Page is not generated", HttpStatus.NO_CONTENT);
    }
    @PutMapping("/edit/**")
    public ResponseEntity<String> putWikiPage(HttpServletRequest request, @RequestBody String body) throws Exception {
        String path = request.getRequestURI();
        String p = path.replaceAll("\\/edit\\/","\\/wiki\\/");
        String key = request.getParameter("key");
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        if(key==null)
            return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
        Token t = tokens.findByToken(key);
        if (t == null)
            return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);
        User u = t.getUser();
        if (u == null)
            return new ResponseEntity<>("{\"ok\":false}", HttpStatus.OK);

        Cache.addPageCache(p,body);
        Cache.pageLog(p,"\nPage changed by user with id:"+u.getId()+"\n"+body);
        return new ResponseEntity<>("{\"ok\":true}", HttpStatus.OK);
    }
}
