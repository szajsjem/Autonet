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
        String wikiPage = llm.completeText("You are a api that returns a basic http page with a wiki-like layout, add links to other pages within the wiki like \"/wiki/{page about}\"",
                "<html><body><p>Page about:"+path+"</p><br>");
        NginxCache.addPageCache(path,wikiPage);
        return new ResponseEntity<>(wikiPage, HttpStatus.OK);
    }

    @GetMapping("/wiki/**")
    public ResponseEntity<String> getWikiPage(HttpServletRequest request) {
        String path = request.getRequestURI();
        path = path.replace("/wiki/","");
        return preparePage(path);
    }
    @GetMapping("/wiki")
    public ResponseEntity<String> getWiki(@RequestParam String search) {
        return preparePage(search);
    }
}
