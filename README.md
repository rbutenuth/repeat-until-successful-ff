# Repeat Until Successful - Fail Fast

## Introduction

The Repeat until successful module is similar to [Until Successful from MuleSoft](https://docs.mulesoft.com/mule-runtime/4.4/until-successful-scope). 

There are two additional features:
* The delay time between successive repeats can be computed by a DataWeave expression, e.g. to implemenent exponential backoff.
* You can fail fast for some errors. Which ones is determined by a regular expression which tries to match NAMESPACE:IDENTIFIER of the error.

Configuration parameters:
* numberOfRetries (mandatory): How often shall the operation be retried when the first try failed?
* initialDelay (mandatory): Time between initial call and first retry, in milliseconds.
* followUpDelay (optional): A DataWeave expression to compute the wait time, starting with the second retry. 
When expression is empty, initialDelay will be used for all delays.
The following predefined variables can be used in the DataWeave:
  * initialDelay: Delay between initial call and first retry
  * lastDelay: Last delay in millisedonds
  * retryIndex: Which retry is this (count starts at 0)
* failFastPattern (optional): When error matches the given regex (and mode is FAIL_ON_MATCH), not retries will be done.
  When mode is FAIL_ON_NOT_MATCH, the matcher reverses its definition.
* mode FAIL_ON_MATCH: Fail fast on match of failFastPattern (default), FAIL_ON_NOT_MATCH: Fail fast on not match of failFastPattern.

When the number of tries is exhausted or the failFastPattern has matched, the last of the occuring exceptions is propagated out of the scope.

## Example

Start with 1000ms initial delay, do two retries in case initial call failed, increase delay time by a factor of two between retries:

```
<untilsuccessfulfailfast:repeat-until-successful-ff doc:name="Repeat until successful ff" 
	numberOfRetries="2" 
	initialDelay="1000" 
	followUpDelay="#[lastDelay * 2]" 
	failFastPattern="MY_NAMESPACE:MY_TYPE">
...
</untilsuccessfulfailfast:repeat-until-successful-ff>
```

## Maven Configuration

Add the following dependency to your pom.xml:

```
<dependency>
	<groupId>de.codecentric.mule.modules</groupId>
	<artifactId>repeat-until-successful-ff</artifactId>
	<version>1.2.0</version>
	<classifier>mule-plugin</classifier>
</dependency>
```

## Release notes

* 1.2.1 2024-07-22: Updated Mule Java SDK to 1.6.3, now ready for Mule running on Java 17.
* 1.1.1 2023-04-25: Dependency updates
* 1.1.0 2022-05-11: Failure modes
* 1.0.0 2021-11-30: Initial release.

 
