package de.codecentric.mule.rusff.internal;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import de.codecentric.mule.rusff.api.RepeatErrorType;

import static java.util.Collections.singleton;
import java.util.Set;

public class NumberErrorType implements ErrorTypeProvider {

	@SuppressWarnings("rawtypes")
	@Override
	public Set<ErrorTypeDefinition> getErrorTypes() {
		return singleton(getErrorType());
	}

	/**
	 * Defines the error type to throw. Subclasses can override this as desired.
	 *
	 * @return the error type to declare as thrown
	 */
	protected RepeatErrorType getErrorType() {
		return RepeatErrorType.INVALID_NUMBER;
	}

}
