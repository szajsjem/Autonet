package pl.szajsjem.autonet.LLM;

public interface LLM {
    String completeText(String systemText, String retBegining);
    String chat(String[] messages) throws Exception;
    String name();

}
