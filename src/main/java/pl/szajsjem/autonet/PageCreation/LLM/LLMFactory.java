package pl.szajsjem.autonet.PageCreation.LLM;

import pl.szajsjem.autonet.PageCreation.LLM.LLMimpl.GPT3d5;
import pl.szajsjem.autonet.PageCreation.LLM.LLMimpl.GPT4;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

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
