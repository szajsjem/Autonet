package pl.szajsjem.autonet.PageCreation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.szajsjem.autonet.DB.NginxCache;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;
import pl.szajsjem.autonet.PageCreation.LLM.LLMFactory;

@Controller
public class FullPage {


    @GetMapping("/wiki/{path}")
    public ResponseEntity<String> getTopicList(@PathVariable String path) {
        LLM llm = LLMFactory.getLLM("GPT3.5");
        assert llm != null;
        String wikiPage = llm.completeText("You are a api that returns a basic http page with a wiki-like layout, add links to other pages within the wiki like \"/wiki/{page about}\"",
                "<html><body><p>Page about:"+path+"</p><br>");
        NginxCache.addPageCache(path,wikiPage);
        return new ResponseEntity<>(wikiPage, HttpStatus.OK);
    }
}
