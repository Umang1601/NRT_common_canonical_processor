package prama.ai.canonical.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.mapper.model.MappingModel;

@Getter
public  class ProcessorConfig {

    private final String name;

    private final String topic;

    private final String collection;

    private final MappingModel model;

    private final String[] keys;

    private final DefaultProcessor processor;

    public ProcessorConfig(String name, String topic, String collection,
                           DefaultProcessor processor, MappingModel model, String[] keys) {
        System.out.println("In ProcessorConfig Constructor");
        this.name = name;
        this.topic = topic;
        this.collection = collection;
        this.processor = processor;
        this.model = model;
        this.keys = keys;
    }
}
