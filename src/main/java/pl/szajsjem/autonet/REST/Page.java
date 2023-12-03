package pl.szajsjem.autonet.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.szajsjem.autonet.DB.DTO.RestResponse;
import pl.szajsjem.autonet.DB.jpa.PageRepository;

import java.util.Map;

@RestController
public class Page {
    @Autowired
    PageRepository pages;

    @PostMapping("/api/isGenerated")
    RestResponse pageStatus(@RequestBody Map<String,String> map){
        if(!map.containsKey("page"))return new RestResponse(false, null, "Invalid request");
        var t = pages.findByUrl(map.get("page"));
        if(t!=null) {
            if (t.isGenerated())
                return new RestResponse(true, null, null);
            else if (t.getErrorMessage() != null)
                return new RestResponse(false, null, t.getErrorMessage());
        }
        return new RestResponse(false, null, null);
    }

}
