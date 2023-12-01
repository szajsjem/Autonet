package pl.szajsjem.autonet.DB.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    public String model;
    public String path;
    public String user;
    public String system;
}
