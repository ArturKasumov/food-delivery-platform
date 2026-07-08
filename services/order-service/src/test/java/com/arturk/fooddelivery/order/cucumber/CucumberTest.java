package com.arturk.fooddelivery.order.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters(value = {
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.arturk.fooddelivery.order.cucumber.steps"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber/cucumber-report.html, json:target/cucumber/cucumber-report.json")})
class CucumberTest {
}
