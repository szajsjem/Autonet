package pl.szajsjem.autonet.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.szajsjem.autonet.DB.DTO.RestResponse;
import pl.szajsjem.autonet.DB.jpa.PageRepository;

@RestController
public class Page {
    @Autowired
    PageRepository pages;

    @PostMapping("/api/isGenerated")
    RestResponse pageStatus(@RequestBody String page){
        var t = pages.findByUrl(page);
        if(t.isGenerated())
            return new RestResponse(true,null,null);
        else
            if(t.getErrorMessage()!=null)
                return new RestResponse(false,"error",t.getErrorMessage());
        return new RestResponse(false, null, "Page is in construction");
    }

}
