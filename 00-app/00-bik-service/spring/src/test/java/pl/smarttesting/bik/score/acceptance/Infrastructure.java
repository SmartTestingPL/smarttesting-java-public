package pl.smarttesting.bik.score.acceptance;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

// Dotyczy lekcji 04-01
interface Infrastructure {

	@Container
	RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.8.27-management-alpine")
			.withReuse(true);

	@Container
	MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.4")
			.withReuse(true);

	WireMockServer incomeService = new WireMockServer(WireMockConfiguration.options().dynamicPort());

	WireMockServer monthlyCostService = new WireMockServer(WireMockConfiguration.options().dynamicPort());

	WireMockServer personalService =  new WireMockServer(WireMockConfiguration.options().dynamicPort());

	WireMockServer socialService =  new WireMockServer(WireMockConfiguration.options().dynamicPort());

	static void startHttpServers() {
		incomeService.start();
		monthlyCostService.start();
		personalService.start();
		socialService.start();
	}

	@BeforeAll
	static void setup() {
		stubIncomeService();
		stubMonthlyCostService();
		stubPersonalService();
		stubSocialService();
	}

	@AfterAll
	static void shutdown() {
		incomeService.shutdown();
		monthlyCostService.shutdown();
		personalService.shutdown();
		socialService.shutdown();
	}

	private static void stubSocialService() {
		socialService.stubFor(get(urlMatching("/[0-9]{11}")).willReturn(aResponse().withStatus(200).withBody("""
				{ "noOfDependants": 2, "noOfPeopleInTheHousehold": 3, "maritalStatus": "MARRIED",  "contractType" : "EMPLOYMENT_CONTRACT" }
				""").withHeader("Content-Type", "application/json")));
		socialService.stubFor(get("/12345678901").willReturn(aResponse().withStatus(200).withBody("""
				{ "noOfDependants": 20, "noOfPeopleInTheHousehold": 30, "maritalStatus": "MARRIED",  "contractType" : "UNEMPLOYED" }
				""").withHeader("Content-Type", "application/json")));
	}

	private static void stubPersonalService() {
		personalService.stubFor(get(urlMatching("/[0-9]{11}")).willReturn(aResponse().withStatus(200).withBody("""
				{ "education":"HIGH", "yearsOfWorkExperience":10, "occupation": "PROGRAMMER" }
				""").withHeader("Content-Type", "application/json")));
		personalService.stubFor(get("/1234567890").willReturn(aResponse().withStatus(200).withBody("""
				{ "education":"NONE", "yearsOfWorkExperience":1, "occupation": "OTHER" }
				""").withHeader("Content-Type", "application/json")));
	}

	private static void stubMonthlyCostService() {
		monthlyCostService.stubFor(get(urlMatching("/[0-9]{11}")).willReturn(aResponse().withStatus(200).withBody("800").withHeader("Content-Type", "application/json")));
		monthlyCostService.stubFor(get("/12345678901").willReturn(aResponse().withStatus(200).withBody("100000").withHeader("Content-Type", "application/json")));
	}

	private static void stubIncomeService() {
		incomeService.stubFor(get(urlMatching("/[0-9]{11}")).willReturn(aResponse().withStatus(200).withBody("500").withHeader("Content-Type", "application/json")));
		incomeService.stubFor(get("/12345678901").willReturn(aResponse().withStatus(200).withBody("0").withHeader("Content-Type", "application/json")));
	}

}
