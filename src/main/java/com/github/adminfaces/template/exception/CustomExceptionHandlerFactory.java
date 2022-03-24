package com.github.adminfaces.template.exception;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {

	private final ExceptionHandlerFactory parent;

	public CustomExceptionHandlerFactory(final ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new CustomExceptionHandler(this.parent.getExceptionHandler());
	}

}