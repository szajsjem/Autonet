package pl.szajsjem.autonet.DB;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Cache {
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
    public static void pageLog(String path, String page){
        try (
                FileOutputStream outputStream = new FileOutputStream(cachePath+"/log"+path,true);
        ) {
            outputStream.write(page.getBytes());
        }catch (IOException ignored) {
        }
    }
    public static String getPageCache(String path){
        try (
                FileInputStream inputStream = new FileInputStream(cachePath+path);
        ) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException ignored) {
            return "";
        }
    }

}
