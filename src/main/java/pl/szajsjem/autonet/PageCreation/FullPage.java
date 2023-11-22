package pl.szajsjem.autonet.PageCreation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.szajsjem.autonet.DB.NginxCache;
import pl.szajsjem.autonet.DB.entity.Token;
import pl.szajsjem.autonet.DB.entity.User;
import pl.szajsjem.autonet.DB.jpa.TokenRepository;
import pl.szajsjem.autonet.DB.jpa.UserRepository;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;
import pl.szajsjem.autonet.PageCreation.LLM.LLMFactory;
import pl.szajsjem.autonet.REST.Profile;

@Controller
public class FullPage {
    @Autowired
    private UserRepository users;
    @Autowired
    private TokenRepository tokens;

    public ResponseEntity<String> preparePage(String path, String key) throws Exception {
        if(key==null){
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            key = (String) session.getAttribute("token");
        }
        String model=null, system=null, user=null;
        if(key!=null){
            Token t = tokens.findByToken(key);
            if(t != null){
                model = t.getUser().getSelectedModel();
                system = t.getUser().getPageGenSystemText();
                user = t.getUser().getPageGenUserText();
            }
        }

        if(model==null)
            model = Profile.getDefaultModel();
        if(system==null)
            system = Profile.getDefaultSystemPrompt();
        if(user==null)
            user = Profile.getDefaultUserRequest();

        LLM llm = LLMFactory.getLLM(model);
        assert llm != null;
        //String wikiPage = llm.completeText("Create a html document with content that matches the following URL path: "+path+"\nAdd href links with relative paths to related topics",
        //        "<!DOCTYPE html>\n<html>\n<head>\n<title>AI wiki</title>\n</head>\n<body>\n");
        var splitusermessage = user.split("\\$\\$URL\\$\\$");
        if(splitusermessage.length!=2)
            return new ResponseEntity<>("invalid use of $$URL$$", HttpStatus.OK);
        String userMessage = splitusermessage[0] + path + splitusermessage[1]+"\nPlease start relative links with /wiki/(main topic)/(subtopic) and start your response with:\n<!DOCTYPE html>\n<html>\n<head>\n<title>AI wiki</title>\n</head>\n<body>\n";
        String wikiPage = llm.chat(new String[]{system,user});
        String[] spl = wikiPage.split("</head>\n<body>");
        wikiPage = spl[0]+navigation+spl[1];
        NginxCache.addPageCache(path,wikiPage);
        return new ResponseEntity<>(wikiPage, HttpStatus.OK);
    }

    @GetMapping("/wiki/**")
    public ResponseEntity<String> getWikiPage(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        String key = request.getParameter("key");
        return preparePage(path,key);
    }
    @GetMapping("/wiki")
    public ResponseEntity<String> getWiki(@RequestParam String search,@RequestParam(required = false) String key) throws Exception {
        return preparePage("/wiki/"+search,key);
    }



    static final String navigation = """
            <style>
              /* Body Styles */
              body {
                font-family: Arial, sans-serif;
                background-color: #f2f2f2;
                text-align: center;
                margin: 0;
                padding: 0;
              }
                        
              /* Header Styles */
              h2 {
                background-color: #333;
                color: #fff;
                padding: 20px;
                margin: 0;
              }
                        
              /* Navbar Styles */
              #topNavbar {
                  background-color: #333;
                  color: #fff;
                border-bottom: 2px solid #357ABD; /* Bottom border to separate navbar from content */
                padding: 10px;
              }
                        
              #topNavbar button {
                background-color: #fff; /* Button background color in the navbar */
                color: #4285F4; /* Button text color in the navbar */
                border: none;
                border-radius: 5px;
                padding: 10px 20px;
                cursor: pointer;
                margin-right: 10px;
              }
                        
              #topNavbar button:hover {
                background-color: #ddd; /* Button background color on hover */
              }
                        
              /* Form Styles */
              form {
                background-color: #fff;
                border: 1px solid #ccc;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                padding: 20px;
                margin: 20px auto;
                max-width: 300px;
                text-align: center;
              }
                        
              /* Input Styles */
              input[type="text"] {
                width: 100%;
                padding: 10px;
                margin: 10px 0;
                border: 1px solid #ccc;
                border-radius: 5px;
                box-shadow: none;
              }
                        
              input[type="submit"] {
                background-color: #4285F4; /* Change to your desired color */
                color: #fff;
                border: none;
                border-radius: 5px;
                padding: 10px 20px;
                cursor: pointer;
                transition: background-color 0.3s;
              }
                        
              input[type="submit"]:hover {
                background-color: #357ABD; /* Change to your desired hover color */
              }
            </style></head>
            <body>
            <nav id="topNavbar">
                           <!-- Navbar content will be updated here -->
                       </nav>
                       <script>
                           function fetchData(url, callback) {
                             fetch(url)
                               .then(response => response.json())
                               .then(data => callback(data))
                               .catch(error => console.error('Error:', error));
                           }
                           function fetchPost(url, callback) {
                             fetch(url, {
                                   method: 'POST'
                               })
                               .then(response => response.json())
                               .then(data => callback(data))
                               .catch(error => console.error('Error:', error));
                           }
                           function fetchDelete(url, callback) {
                             fetch(url, {
                                   method: 'DELETE'
                               })
                               .then(data => callback(data))
                               .catch(error => console.error('Error:', error));
                           }
                       
                       
                           function updateNavbar() {
                             const navbar = document.getElementById('topNavbar');
                             fetchData('/api/user/testlogin', (loginStatus) => {
                               if (loginStatus.ok === true) {
                                 fetchData('/api/user', (userData) => {
                                   navbar.innerHTML = `
                                     <span>Welcome, ${userData.data.login}!</span>
                                     <button onclick="goToProfile()">Profile</button>
                                     <button onclick="logout()">Logout</button>
                                   `;
                                 });
                               } else {
                                 navbar.innerHTML = `
                                   <button onclick="goToLogin()">Login</button>
                                   <button onclick="goToRegister()">Register</button>
                                 `;
                               }
                             });
                           }
                           function goToLogin() {
                             window.location.href = '/login';
                           }
                           function goToRegister() {
                             window.location.href = '/register';
                           }
                           function goToProfile() {
                             window.location.href = '/profile';
                           }
                           function logout() {
                               fetchDelete('/api/user/logout',(data)=>{
                                   window.location.href = '/';
                               });
                           }
                       
                           updateNavbar();
                         </script>""";
}
