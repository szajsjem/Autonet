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

    final String systemPrompt= """
            Given the full URL, please perform the following tasks:
            1. *Extract and Summarize Content:* Retrieve the content from the specified URL and create a concise summary that captures the main points and purpose of the page. Ensure the summary is structured with a clear introduction, body, and conclusion reflecting the original content's organization.
            2. *Identify Relevant Links:* As you create the summary, identify key concepts and topics that are extensively discussed on the page. For each of these, find relevant internal links (links to other pages within the same domain) that provide additional context or further information.
            3. *Insert Contextual Links:* Embed these links seamlessly into the summary text where they naturally fit. The anchor text for each link should be informative and indicate the linked page's content, maintaining the coherence and context of the original page.
            4. *Maintain Original Context:* Be careful to preserve the original context of the page. Do not insert links that could mislead or take the reader on a tangent unrelated to the primary focus of the original content.
            Please provide the summary along with the contextually embedded links in html format.
            """;
    final String userRequest= """
            AI, I am providing you with the full URL of a webpage: $$URL$$. Based on your knowledge up to 2023, please generate a concise summary of the main content of this page, not exceeding 300 words. The summary should capture the key points and themes of the original content. Additionally, incorporate up to five relevant hyperlinks within the summary that enhance the reader's understanding without detracting from the core context of the page. These can be either important internal links to other sections of the same website or external links to authoritative sources that offer additional information on the topics covered. Ensure that the summary remains coherent and flows naturally with the inserted hyperlinks.
            """;

    public ResponseEntity<String> preparePage(String path) {
        LLM llm = LLMFactory.getLLM("GPT3.5");
        assert llm != null;
        String wikiPage = llm.completeText("Create a html document with content that matches the following URL path: "+path+"\nAdd href links with relative paths to related topics",
                "<!DOCTYPE html>\n<html>\n<head>\n<title>AI wiki</title>\n</head>\n<body>\n");
        String[] spl = wikiPage.split("</head>\n<body>");
        wikiPage = spl[0]+navigation+spl[1];
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
