package pl.szajsjem.autonet.PageCreation.LLM.LLMimpl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class GPT3d5 implements LLM {
    OpenAiService service;
    @Override
    public String completeText(String systemText, String retBegining) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(systemText+"\n"+retBegining)
                .echo(false)
                .n(1)
                .maxTokens(512)
                .temperature(0.2)
                .build();
        String response = service.createCompletion(completionRequest).getChoices().get(0).getText();
        return retBegining+ response;
    }

    @Override
    public String chat(String[] message) throws Exception {
        if(message.length<2)throw new Exception("Not enough messages");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), message[0]);
        messages.add(systemMessage);
        for(int i=1;i<message.length;i++) {
            final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message[i]);
            messages.add(systemMessage);
        }
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(512)
                .logitBias(new HashMap<>())
                .build();
        String response = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        return response;
    }

    @Override
    public String name() {
        return "GPT3.5";
    }
}
