/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package de.codecentric.mule.rusff.api;


import static java.util.Optional.of;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

/**
 * List of {@link ErrorTypeDefinition} that throws the {@link RepeatModule}
 */
public enum RepeatErrorType implements ErrorTypeDefinition<RepeatErrorType> {

	INVALID_NUMBER(MuleErrors.EXPRESSION)
	;

  private ErrorTypeDefinition<?> parentErrorType;

  RepeatErrorType(ErrorTypeDefinition<?> parentErrorType) {
    this.parentErrorType = parentErrorType;
  }

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return of(parentErrorType);
  }

}
