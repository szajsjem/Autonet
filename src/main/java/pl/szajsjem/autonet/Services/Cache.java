package pl.szajsjem.autonet.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Cache {
    static String cachePath = System.getenv("CACHE_PATH");
    public static void addPageCache(String path,String page){
        if(!path.startsWith("/wiki/"))return;
        File file = new File(cachePath+path);
        file.mkdirs();
        if(file.isDirectory())
            file.delete();
        if(page==null){
            file.delete();
        }
        else
            try (
                    FileOutputStream outputStream = new FileOutputStream(file);
            ) {
                outputStream.write(page.getBytes());
            }catch (IOException ignored) {
            }
    }
    public static void pageLog(String path, String page){
        if(!path.startsWith("/wiki/"))return;
        File file = new File(cachePath+"/log"+path);
        file.mkdirs();
        if(file.isDirectory())
            file.delete();
        try (
                FileOutputStream outputStream = new FileOutputStream(file,true);
        ) {
            outputStream.write(page.getBytes(StandardCharsets.UTF_8));
        }catch (IOException ignored) {
        }
    }
    public static String getPageCache(String path){
        if(!path.startsWith("/wiki/"))return "";
        File file = new File(cachePath+path);
        file.mkdirs();
        if(file.isDirectory())
            file.delete();
        try (
                FileInputStream inputStream = new FileInputStream(file);
        ) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException ignored) {
            return "";
        }
    }
    public static String getPageLog(String path){
        File file = new File(cachePath+path);
        file.mkdirs();
        if(file.isDirectory())
            file.delete();
        try (
                FileInputStream inputStream = new FileInputStream(file);
        ) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException ignored) {
            return "";
        }
    }

}
