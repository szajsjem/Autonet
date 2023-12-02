package pl.szajsjem.autonet.Services.LLM.LLMimpl;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import pl.szajsjem.autonet.Services.LLM.LLM;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GPT4 implements LLM {
    OpenAiService service;

    public String completeMessage(String systemText, String message){
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemText);
        messages.add(systemMessage);
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4-1106-preview")
                .messages(messages)
                .maxTokens(2048)
                .build();
        String response = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        return response;
    }

    @Override
    public String completeText(String systemText, String retBegining) {
        return completeMessage(systemText, "Begin your response with:\n"+retBegining);
    }

    @Override
    public String chat(String[] messages) {
        if(messages.length<2)throw new IllegalArgumentException("Not enough messages");
        if(messages.length == 2)return completeText(messages[0], messages[1]);
        return "not implemented";
    }

    @Override
    public String name() {
        return "GPT4";
    }
}
