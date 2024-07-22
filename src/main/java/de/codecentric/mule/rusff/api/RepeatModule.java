package de.codecentric.mule.rusff.api;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import de.codecentric.mule.rusff.internal.NumberErrorType;

@Extension(name = "Until successful fail fast")
@Operations(RepeatOperations.class)
@ErrorTypes(RepeatErrorType.class)
@Throws(NumberErrorType.class)
@JavaVersionSupport({ JAVA_8, JAVA_11, JAVA_17})
public class RepeatModule {

}
