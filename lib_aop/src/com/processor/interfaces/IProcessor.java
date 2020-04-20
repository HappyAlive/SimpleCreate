package com.processor.interfaces;

import com.processor.AnnotationProcessor;

import javax.annotation.processing.RoundEnvironment;

/**
 * 注解处理器接口
 */
public interface IProcessor {
    void process(AnnotationProcessor mAbstractProcessor, RoundEnvironment roundEnv);
}
