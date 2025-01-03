package com.example;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;

import nablarch.core.repository.di.ComponentFactory;

public class OpenAIClientFactory implements ComponentFactory<OpenAIClient> {

    private String openAiApiKey;

    @Override
    public OpenAIClient createObject() {
        return new OpenAIClientBuilder()
                .credential(new KeyCredential(openAiApiKey))
                .buildClient();
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }
}
