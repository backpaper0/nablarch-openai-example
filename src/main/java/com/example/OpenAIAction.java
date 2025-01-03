package com.example;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.jaxrs.JaxRsHttpRequest;
import java.util.List;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/openai")
public class OpenAIAction {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object findProducesJson(JaxRsHttpRequest req) {
        String query = req.getParam("query")[0];
        OpenAIClient openaiClient = SystemRepository.get("openai");
        List<ChatRequestMessage> messages = List.of(
                new ChatRequestUserMessage(query));
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        ChatCompletions completions = openaiClient.getChatCompletions("gpt-4o-mini", options);
        return completions.getChoices().get(0);
    }
}
