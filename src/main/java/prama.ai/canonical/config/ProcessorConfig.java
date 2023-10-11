package prama.ai.canonical.config;

import lombok.Getter;
import prama.ai.canonical.processor.DefaultProcessor;
import prama.ai.mapper.model.MappingModel;

@Getter
public  class ProcessorConfig {

    private final DefaultProcessor processor;

    private final MappingModel model;

    private final String[] keys;

    public ProcessorConfig(DefaultProcessor processor, MappingModel model, String[] keys) {
        this.processor = processor;
        this.model = model;
        this.keys = keys;
    }
}
