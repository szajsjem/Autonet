package pl.szajsjem.autonet.DB.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse {
    boolean ok;
    Object data;
    String message;
}
