package pl.szajsjem.autonet.PageCreation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szajsjem.autonet.DB.NginxCache;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;
import pl.szajsjem.autonet.PageCreation.LLM.LLMFactory;

@Controller
public class FullPage {

    public ResponseEntity<String> preparePage(String path) {
        LLM llm = LLMFactory.getLLM("GPT3.5");
        assert llm != null;
        String wikiPage = llm.completeText("Create a html document with content that matches the following URL path: "+path+"\nAdd href links with relative paths to related topics",
                "<!DOCTYPE html>\n<html>\n<head>\n<title>AI wiki</title>\n</head>\n<body>\n");
        String[] spl = wikiPage.split("<body>");
        wikiPage = spl[0]+"<body>"+navigation+spl[1];
        NginxCache.addPageCache(path,wikiPage);
        return new ResponseEntity<>(wikiPage, HttpStatus.OK);
    }

    @GetMapping("/wiki/**")
    public ResponseEntity<String> getWikiPage(HttpServletRequest request) {
        String path = request.getRequestURI();
        return preparePage(path);
    }
    @GetMapping("/wiki")
    public ResponseEntity<String> getWiki(@RequestParam String search) {
        return preparePage("/wiki/"+search);
    }



    static final String navigation = """
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

                function updateNavbar() {
                  const navbar = document.getElementById('topNavbar');
                  fetchData('/api/testlogin', (loginStatus) => {
                    if (loginStatus.loggedIn) {
                      fetchData('/api/user', (userData) => {
                        navbar.innerHTML = `
                          <span>Welcome, ${userData.username}!</span>
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
                  fetchData('/api/logout', (status) => {
                    window.location.href = '/';
                  }
                }

                updateNavbar();
              </script>""";
}
