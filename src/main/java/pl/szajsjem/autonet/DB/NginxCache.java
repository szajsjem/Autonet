package pl.szajsjem.autonet.DB;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NginxCache {
    static String cachePath = System.getenv("CACHE_PATH");
    public static void addPageCache(String path,String page){
        if(!path.startsWith("/wiki/"))return;
        try (
                FileOutputStream outputStream = new FileOutputStream(cachePath+path);
        ) {
            outputStream.write(page.getBytes());
        }catch (IOException ignored) {
        }
    }
}
