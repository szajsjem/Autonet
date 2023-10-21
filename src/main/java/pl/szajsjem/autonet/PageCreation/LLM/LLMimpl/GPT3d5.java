package pl.szajsjem.autonet.PageCreation.LLM.LLMimpl;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;
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
                .temperature(0.7)
                .build();
        String response = service.createCompletion(completionRequest).getChoices().get(0).getText();
        return retBegining+ response;
    }

    @Override
    public String chat(String[] messages) {
        return "not implemented yet";
    }

    @Override
    public String name() {
        return "GPT3.5";
    }
}
