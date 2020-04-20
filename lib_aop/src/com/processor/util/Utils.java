package com.processor.util;

import com.processor.exception.NoPackageNameException;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 工具类
 */
public class Utils {
    public static final String PackageName = "com.apt";
    public static final String ANNOTATION = "@";

    /**
     * 该方法是否为公共方法
     *
     * @param element 类型
     * @return true--公共方法,false--私有
     */
    public static boolean isPublic(TypeElement element) {
        return element.getModifiers().contains(Modifier.PUBLIC);
    }

    public static boolean isAbstract(TypeElement element) {
        return element.getModifiers().contains(Modifier.ABSTRACT);
    }

    /**
     * 判断类是否有效
     */
    public static boolean isValidClass(Messager messager, TypeElement element) {
        if (element.getKind() != ElementKind.CLASS) {
            return false;
        }
        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.", ANNOTATION);
//            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }
        if (isAbstract(element)) {
            String message = String.format("Classes annotated with %s must not be abstract.", ANNOTATION);
//            messager.printMessage(Diagnostic.Kind.NOTE, message, element);
            return false;
        }
        return true;
    }

    public static String getPackageName(Elements elements, TypeElement typeElement) throws NoPackageNameException {
        PackageElement pkg = elements.getPackageOf(typeElement);
        if (pkg.isUnnamed()) {
            throw new NoPackageNameException(typeElement);
        }
        return pkg.getQualifiedName().toString();
    }


    public static String getClassName(TypeElement typeElement) throws ClassNotFoundException {
        return ClassName.get(typeElement).simpleName();
    }

    public static ClassName getType(String className) {
        return ClassName.get(className.substring(0, className.lastIndexOf(".")),
                className.substring(className.lastIndexOf(".") + 1, className.length()));
    }
}
