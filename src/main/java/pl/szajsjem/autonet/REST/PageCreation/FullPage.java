package pl.szajsjem.autonet.REST.PageCreation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.szajsjem.autonet.DB.DTO.PageRequest;
import pl.szajsjem.autonet.DB.entity.GenInstructions;
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;
import pl.szajsjem.autonet.DB.jpa.TokenRepository;
import pl.szajsjem.autonet.DB.jpa.UserRepository;
import pl.szajsjem.autonet.REST.Profile;
import pl.szajsjem.autonet.Services.PageCreationService;

import static pl.szajsjem.autonet.DB.Cache.pageLog;

@Controller
public class FullPage {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;
    @Autowired
    private PageCreationService pageCreationService;


    public ResponseEntity<String> preparePage(String path, String key) throws Exception {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        String model=null, system=null, user=null;
        if(key!=null) {
            Token t = tokens.findByToken(key);
            if (t != null) {
                User u = t.getUser();
                if (u != null) {
                    GenInstructions g = u.getGenerationInstructions();
                    if (g != null) {
                        model = g.getSelectedModel();
                        system = g.getPageGenSystemText();
                        user = g.getPageGenUserText();
                    }
                }
            }
        }

        if(model==null)
            model = Profile.getDefaultModel();
        if(system==null)
            system = Profile.getDefaultSystemPrompt();
        if(user==null)
            user = Profile.getDefaultUserRequest();

        PageRequest pageRequest= new PageRequest(model,path,user,system);

        pageCreationService.addToQueue(pageRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/topic?page="+path);
        return new ResponseEntity<String>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/wiki/**")
    public ResponseEntity<String> getWikiPage(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        String key = request.getParameter("key");
        var r = preparePage(path,key);
        pageLog(path,r.toString());
        return r;
    }

}
