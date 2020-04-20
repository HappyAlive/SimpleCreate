package com.processor;

import com.processor.apt.InstanceFactory;
import com.processor.interfaces.IProcessor;
import com.processor.util.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * 实例化工厂注解解释器
 */
public class InstanceProcessor implements IProcessor {
    @Override
    public void process(AnnotationProcessor mAbstractProcessor, RoundEnvironment roundEnv) {
        String CLASS_NAME = InstanceFactory.class.getSimpleName();//类名简称
        /*类*/
        TypeSpec.Builder typeBuilder = TypeSpec
                .classBuilder(CLASS_NAME)//java类名
                .addModifiers(PUBLIC, FINAL)//public final
                .addJavadoc("此类为apt自动生成" + CLASS_NAME);
        /*方法*/
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("create")//方法名
//                .addAnnotation(MemoryCache.class)//为方法添加注解
                .addJavadoc("此方法由apt自动生成")//方法注释
                .returns(Object.class)//返回值类型
                .addModifiers(PUBLIC, STATIC)//添加修饰符 public static
                .addException(IllegalAccessException.class)//抛出异常
                .addException(InstantiationException.class)////抛出异常
                .addParameter(Class.class, "mClass");//方法参数以及参数命名
        List<ClassName> classNames = new ArrayList<>();
        //开始一个控制流
        CodeBlock.Builder blockBuilder = CodeBlock.builder().beginControlFlow("switch(mClass.getSimpleName())");
        try {
            for (TypeElement element : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(InstanceFactory.class))) {
                mAbstractProcessor.mMessager.printMessage(Diagnostic.Kind.NOTE, "正在处理: " + element.toString());
                if (!Utils.isValidClass(mAbstractProcessor.mMessager, element)) {
                    continue;
                }
                ClassName currentType = ClassName.get(element);
                if (classNames.contains(currentType)) {
                    continue;
                }
                classNames.add(currentType);
                //添加一行代码，最后会自动添加分号";"
                blockBuilder.addStatement("case $S: return new $T()", currentType.simpleName(), currentType);//创建对象
            }
            blockBuilder.addStatement("default: return mClass.newInstance()");//找不到对应的类,反射创建对象

            blockBuilder.endControlFlow();
            methodBuilder.addCode(blockBuilder.build());
            typeBuilder.addMethod(methodBuilder.build());
            JavaFile javaFile = JavaFile.builder(Utils.PackageName, typeBuilder.build()).build();// 生成源代码
            javaFile.writeTo(mAbstractProcessor.mFiler);// 在 app module/build/generated/source/apt 生成一份源代码
        } catch (FilerException e) {
            System.out.println("FilerException:" + e.toString());
        } catch (IOException e) {
            System.out.println("IOException" + e.toString());
        } catch (Exception e) {
            System.out.println("Exception" + e.toString());
        }
    }
}
