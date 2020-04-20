package com.processor.exception;

import javax.lang.model.element.TypeElement;

public class NoPackageNameException extends Exception {
    private static final long serialVersionUID = -3593295629577377345L;

    public NoPackageNameException(TypeElement typeElement) {
        super("The package of " + typeElement.getSimpleName() + " has no name");
    }
}
