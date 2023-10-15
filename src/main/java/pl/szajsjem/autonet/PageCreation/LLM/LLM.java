package pl.szajsjem.autonet.PageCreation.LLM;

public interface LLM {
    String completeText(String systemText, String retBegining);
    String chat(String[] messages);
    String name();

}
