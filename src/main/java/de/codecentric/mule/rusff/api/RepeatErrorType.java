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
