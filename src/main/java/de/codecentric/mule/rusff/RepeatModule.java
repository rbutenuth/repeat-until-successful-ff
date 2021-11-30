package de.codecentric.mule.rusff;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;

@Extension(name = "Until successful fail fast")
@Operations(RepeatOperations.class)
public class RepeatModule {

}
