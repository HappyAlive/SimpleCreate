package com.processor;

import com.google.auto.service.AutoService;
import com.processor.apt.InstanceFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 *
 */
@AutoService(Processor.class)//自动生成 javax.annotation.processing.IProcessor 文件
@SupportedSourceVersion(SourceVersion.RELEASE_8)//通过jdk1.8编辑
public class AnnotationProcessor extends AbstractProcessor {
    /**
     * 文件相关的辅助类
     */
    public Filer mFiler;
    /**
     * 元素相关辅助类
     */
    public Elements mElements;
    /**
     * 日志相关辅助类
     */
    public Messager mMessager;
    /**
     * 模块名称
     */
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnv.getOptions();
        if (options == null || options.size() == 0) {
        } else {
            moduleName = options.get("moduleName");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        new InstanceProcessor().process(this, roundEnvironment);
        return true;
        /*process 函数返回值表示这组annotations是否被这个Processor接收，
        如果接收后续子的 processor 不会再对这个Annotations进行处理，简单
        点说就是当一个方法被2个注解修饰后，若第一个注解处理器的process方法
        返回了true，那么第二个注解处理器就不会处理该方法了
         */
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(InstanceFactory.class.getName());
        return types;
    }

    public String getModuleName() {
        if (moduleName == null) {
            return "";
        } else {
            return moduleName;
        }
    }
}
