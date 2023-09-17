package pl.smarttesting.bik.score.infrastructure;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.smarttesting.bik.score.analysis.ScoreAnalyzer;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Dotyczy lekcji 03-03
@WebMvcTest(BikController.class)
// Dotyczy lekcji 08-03
@AutoConfigureRestDocs(outputDir = "target/snippets")
class BikControllerTests {

	@Autowired MockMvc mockMvc;

	@Test
	void shouldReturnStatusVerificationPassedForNonFraud() throws Exception {
		this.mockMvc.perform(get("/89050193724"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("status\":\"VERIFICATION_PASSED\"")))
				// Dotyczy lekcji 08-03
				.andDo(document("nonfraud"));
	}

	@Test
	void shouldReturnStatusVerificationFailedForFraud() throws Exception {
		this.mockMvc.perform(get("/00262161334"))
				.andDo(print())
				.andExpect(status().isForbidden())
				.andExpect(content().string(containsString("status\":\"VERIFICATION_FAILED\"")))
				// Dotyczy lekcji 08-03
				.andDo(document("fraud"));
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class Config {

		@Bean
		ScoreAnalyzer testScoreAnalyzer() {
			ScoreAnalyzer analyzer = BDDMockito.mock(ScoreAnalyzer.class);
			given(analyzer.shouldGrantLoan(new Pesel("89050193724"))).willReturn(true);
			given(analyzer.shouldGrantLoan(new Pesel("00262161334"))).willReturn(false);
			return analyzer;
		}
	}
}

