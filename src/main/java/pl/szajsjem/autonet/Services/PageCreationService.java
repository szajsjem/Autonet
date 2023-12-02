package pl.szajsjem.autonet.Services;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import pl.szajsjem.autonet.DB.Cache;
import pl.szajsjem.autonet.DB.DTO.PageRequest;
import pl.szajsjem.autonet.DB.entity.Page;
import pl.szajsjem.autonet.DB.jpa.PageRepository;
import pl.szajsjem.autonet.LLM.LLM;
import pl.szajsjem.autonet.LLM.LLMFactory;

import java.util.*;

@Controller
public class PageCreationService {

    @Autowired
    PageRepository pageRepository;
    List<PageRequest> pagesToCreate = new ArrayList<>();
    public void addToQueue(PageRequest pageRequest) {
        synchronized (this) {
            for (PageRequest p:pagesToCreate){
                if(Objects.equals(p.path, pageRequest.path))
                    return;
            }
            Page page = pageRepository.findByUrl(pageRequest.path);
            pagesToCreate.add(pageRequest);
        }
    }
    private PageRequest getNext() {
        synchronized (this) {
            if(pagesToCreate.size()==0)return null;
            PageRequest p = pagesToCreate.get(0);
            pagesToCreate.remove(0);
            return p;
        }
    }



    public void preparePage(PageRequest pageRequest) {
        LLM llm = LLMFactory.getLLM(pageRequest.model);
        assert llm != null;
        var path = pageRequest.path.replace(' ','-');
        var splitusermessage = pageRequest.user.split("\\$\\$URL\\$\\$");
        StringBuilder userMessage = new StringBuilder();
        for(String s:splitusermessage) {
            userMessage.append(s);
            userMessage.append(path);
        }
        userMessage.replace(userMessage.lastIndexOf(path),userMessage.length(), "\nPlease start relative links with /wiki/(main topic)/(subtopic) and start your response with:\n<!DOCTYPE html>\n<html>\n<head>\n<title>(title)</title>\n</head>\n<body>\n");
        try {
            String wikiPage = llm.chat(new String[]{pageRequest.system,userMessage.toString()});
            String[] spl = wikiPage.split("<\\/head>\n<body>");
            if(spl.length!=2){
                throw new Exception(wikiPage);
            }
            wikiPage = spl[0]+navigation+spl[1];
            Cache.addPageCache(path,wikiPage);
            Page page = pageRepository.findByUrl(pageRequest.path);
            if(page == null) {
                page = new Page(0L, pageRequest.path, true, null);
            }
            page.setGenerated(true);
            page.setErrorMessage(null);
            pageRepository.save(page);
        }
        catch (Exception e){
            Page page = pageRepository.findByUrl(pageRequest.path);
            if(page == null) {
                page = new Page(0L, pageRequest.path, false, e.getMessage());
            }
            page.setGenerated(false);
            page.setErrorMessage(e.getMessage());
            pageRepository.save(page);
        }
    }

    Thread thread =new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                PageRequest pageRequest = getNext();
                if(pageRequest!=null){
                    preparePage(pageRequest);
                }
                else{
                    try {
                        this.wait(1);
                    }
                    catch (Exception ignored){
                    }
                }
            }
        }
    });

    static final String navigation = """
            <link rel="stylesheet" href="style.css">
            </head>
            <body>
            <script src="scripts.js"></script>
            <script>
                updateNavbar();
            </script>
            <nav id="topNavbar">
            </nav>
            """;
}
