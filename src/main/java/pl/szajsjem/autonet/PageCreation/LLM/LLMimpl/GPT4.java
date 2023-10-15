package pl.szajsjem.autonet.PageCreation.LLM.LLMimpl;

import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import pl.szajsjem.autonet.PageCreation.LLM.LLM;

@AllArgsConstructor
public class GPT4 implements LLM {
    OpenAiService service;
    @Override
    public String completeText(String systemText, String retBegining) {
        return retBegining+"gpt4 completion";
    }

    @Override
    public String chat(String[] messages) {
        return "not implemented";
    }

    @Override
    public String name() {
        return "GPT4";
    }
}
