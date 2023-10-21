package pl.szajsjem.autonet.PageCreation;

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

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Controller
public class TopicList {

    @GetMapping("/topic/{path}")
    public ResponseEntity<String> getTopicList(@PathVariable String path) {
        LLM llm = LLMFactory.getLLM("GPT3.5");
        assert llm != null;
        String topicList = llm.completeText("You are a api that returns a basic http page with a list of topics that match the search query with a link to each topic starting like \"/wiki/{topic}\"",
                "<html><body><p>topics matched with page:"+path+"</p><br><ul><li><a href=\"/wiki/");
        NginxCache.addTopicCache(path,topicList);
        return new ResponseEntity<>(topicList, HttpStatus.OK);
    }
    @GetMapping("/topic")
    public ResponseEntity<String> getTopic(@RequestParam String search) {
        return getTopicList(search);
    }

}
