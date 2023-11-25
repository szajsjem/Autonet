package pl.szajsjem.autonet.PageCreation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.szajsjem.autonet.DB.NginxCache;
import pl.szajsjem.autonet.DB.entity.Token;
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
        path = path.replace(' ','-');
        //String wikiPage = llm.completeText("Create a html document with content that matches the following URL path: "+path+"\nAdd href links with relative paths to related topics",
        //        "<!DOCTYPE html>\n<html>\n<head>\n<title>AI wiki</title>\n</head>\n<body>\n");
        var splitusermessage = user.split("\\$\\$URL\\$\\$");
        if(splitusermessage.length!=2)
            return new ResponseEntity<>("invalid use of $$URL$$", HttpStatus.PARTIAL_CONTENT);
        String userMessage = splitusermessage[0]+ path + splitusermessage[1]+"\nPlease start relative links with /wiki/(main topic)/(subtopic) and start your response with:\n<!DOCTYPE html>\n<html>\n<head>\n<title>(title)</title>\n</head>\n<body>\n";
        String wikiPage = llm.chat(new String[]{system,userMessage});
        String[] spl = wikiPage.split("<\\/head>\n<body>");
        if(spl.length!=2){
            return new ResponseEntity<>(wikiPage, HttpStatus.PARTIAL_CONTENT);
        }
        wikiPage = spl[0]+navigation+spl[1];
        NginxCache.addPageCache(path,wikiPage);
        return new ResponseEntity<>(wikiPage, HttpStatus.OK);
    }

    @GetMapping("/wiki/**")
    @Transactional(timeout = 100000)
    public ResponseEntity<String> getWikiPage(HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        String key = request.getParameter("key");
        return preparePage(path,key);
    }
    @GetMapping("/wiki")
    public ResponseEntity<String> getWiki(@RequestParam String search) throws Exception {
        return new ResponseEntity<>("""
                            <!DOCTYPE html>
                            <html lang="en">
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <style>
                                    body {
                                        display: flex;
                                        align-items: center;
                                        justify-content: center;
                                        height: 100vh;
                                        margin: 0;
                                    }
                                
                                    .loader {
                                        border: 8px solid #f3f3f3;
                                        border-top: 8px solid #3498db;
                                        border-radius: 50%;
                                        width: 50px;
                                        height: 50px;
                                        animation: spin 1s linear infinite;
                                    }
                                
                                    @keyframes spin {
                                        0% { transform: rotate(0deg); }
                                        100% { transform: rotate(360deg); }
                                    }
                                </style>
                                <title>Loading Spinner</title>
                            </head>
                            <body>
                <script>
                                                                        // Add a delay before redirecting
                                                                        setTimeout(function() {
                                                                            window.location.href = '/wiki/"""+search+
        """
';}, 1000);
                            </script>
                                <div class="loader"></div>
                            </body>
                            </html>
                """, HttpStatus.OK);
    }



    static final String navigation = """
                <style>
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
                </style>
            </head>
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
