package de.codecentric.mule.rusff.api;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.error.Throws;

import de.codecentric.mule.rusff.internal.NumberErrorType;

@Extension(name = "Until successful fail fast")
@Operations(RepeatOperations.class)
@ErrorTypes(RepeatErrorType.class)
@Throws(NumberErrorType.class)
public class RepeatModule {

}
