package com.example;

import nablarch.core.repository.di.config.externalize.AnnotationComponentDefinitionLoader;

public class ComponentDefinitionLoader extends AnnotationComponentDefinitionLoader {

    @Override
    protected String getBasePackage() {
        return "com.example";
    }
}
