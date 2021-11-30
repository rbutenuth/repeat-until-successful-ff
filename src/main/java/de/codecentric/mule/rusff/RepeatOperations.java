package de.codecentric.mule.rusff;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.parameter.Literal;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.runtime.route.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatOperations implements Stoppable, Startable {
	private static Logger logger = LoggerFactory.getLogger(RepeatOperations.class);

	@Inject
	private ExpressionManager expressionManager;

	@Inject
	private SchedulerService schedulerService;

	private ScheduledExecutorService scheduledExecutor;

	@Alias("repeat-until-successful-ff")
	public void repeat(Chain operations, CompletionCallback<Object, Object> callback, //
			@Summary("How often shall the operation be retried when the first try failed?") int numberOfRetries,
			@Summary("Time between initial call and first retry, in milliseconds") int initialDelay,
			@Summary("A DataWeave expression to compute the wait time, starting with the second retry. "
					+ "The following predefined variables exist: "
					+ "initialDelay: Delay between initial call and first retry, "
					+ "lastDelay: Last delay in millisedonds, " + "retryIndex: Which retry is this. "
					+ "When expression is empty, initialDelay will be used for all delays.") @Optional @Expression(ExpressionSupport.REQUIRED) Literal<String> followUpDelay,
			@Summary("Regular expression for errors (NAMESPACE:TYPE), in case of match, no retry will be done.") @Optional @Expression(ExpressionSupport.NOT_SUPPORTED) String failFastPattern) {

		RepeatRunner repeatRunner = new RepeatRunner(operations, callback, numberOfRetries, initialDelay,
				createFollowUpLiteral(followUpDelay), createOptionalPattern(failFastPattern));
		repeatRunner.run();

	}

	private java.util.Optional<String> createFollowUpLiteral(Literal<String> followUpDelay) {
		if (followUpDelay == null) {
			return java.util.Optional.empty();
		} else {
			return followUpDelay.getLiteralValue();
		}
	}

	private java.util.Optional<Pattern> createOptionalPattern(String failFastPattern) {
		if (StringUtils.isEmpty(failFastPattern)) {
			return java.util.Optional.empty();
		} else {
			return java.util.Optional.of(Pattern.compile(failFastPattern));
		}
	}

	@Override
	public void start() {
		SchedulerConfig config = SchedulerConfig.config().withMaxConcurrentTasks(10)
				.withShutdownTimeout(1, TimeUnit.SECONDS).withPrefix("repeat-until-successful-ff")
				.withName("operations");
		scheduledExecutor = schedulerService.customScheduler(config);
	}

	@Override
	public void stop() {
		scheduledExecutor.shutdown();
	}

	private class RepeatRunner implements Runnable {
		private Chain operations;
		private CompletionCallback<Object, Object> callback;
		private int numberOfRetries;
		private int initialDelay;
		private java.util.Optional<String> followUpDelay;
		private java.util.Optional<Pattern> failFastPattern;
		private int lastDelay;
		private int retryIndex;

		private RepeatRunner(Chain operations, CompletionCallback<Object, Object> callback, //
				int numberOfRetries, int initialDelay, java.util.Optional<String> followUpDelay,
				java.util.Optional<Pattern> failFastPattern) {

			this.operations = operations;
			this.callback = callback;
			this.numberOfRetries = numberOfRetries;
			this.initialDelay = initialDelay;
			lastDelay = initialDelay;
			this.followUpDelay = followUpDelay;
			this.failFastPattern = failFastPattern;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void run() {

			operations.process(result -> {
				logger.info("success, payload = {}", result.getOutput());
				callback.success(result);
			}, (error, previous) -> {
				if (error instanceof MuleException) {
					MuleException exception = (MuleException) error;
					Map<String, Object> info = exception.getInfo();
					String namespaceAndIdentifier = extractErrorType(info);
					if (failFastPattern.isPresent() && failFastPattern.get().matcher(namespaceAndIdentifier).matches()
							|| retryIndex >= numberOfRetries) {
						// fail fast or number of retries reached
						callback.error(error);
					} else {
						// Compute delay and let's try again...
						retryIndex++;
						logger.info("Scheduling try {}", retryIndex);
						if (retryIndex > 1) {
							if (followUpDelay.isPresent()) {
								BindingContext context = BindingContext.builder()
										.addBinding("initialDelay", TypedValue.of(initialDelay))
										.addBinding("lastDelay", TypedValue.of(lastDelay))
										.addBinding("retryIndex", TypedValue.of(retryIndex)).build();
								TypedValue<Number> expressionResult = (TypedValue<Number>) expressionManager
										.evaluateLogExpression(followUpDelay.get(), context);
								lastDelay = expressionResult.getValue().intValue();
							}
						}
						scheduledExecutor.schedule(this, lastDelay, TimeUnit.MILLISECONDS);
					}
				} else {
					callback.error(error);
				}
			});
		}
	}

	static String extractErrorType(Map<String, Object> info) {
		Object errorType = info.get(MuleException.INFO_ERROR_TYPE_KEY);
		if (errorType instanceof String) {
			return (String) errorType;
		} else {
			ErrorType et = (ErrorType) errorType;
			return et.getNamespace() + ":" + et.getIdentifier();
		}
	}
}
