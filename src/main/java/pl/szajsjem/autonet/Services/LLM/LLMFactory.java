package pl.szajsjem.autonet.Services.LLM;

import pl.szajsjem.autonet.Services.LLM.LLMimpl.GPT3d5;
import pl.szajsjem.autonet.Services.LLM.LLMimpl.GPT4;

import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;

public class LLMFactory {
    static String token = System.getenv("OPENAI_TOKEN");
    static OpenAiService service = new OpenAiService(token, Duration.ofSeconds(0));
    public static LLM getLLM(String type) {
        if (type.equals("GPT3.5")) {
            return new GPT3d5(service);
        }
        if (type.equals("GPT4")) {
            return new GPT4(service);
        }
        return null;
    }
}
