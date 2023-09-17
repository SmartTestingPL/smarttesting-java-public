package pl.smarttesting.bik.score.infrastructure;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import pl.smarttesting.bik.score.analysis.ScoreAnalyzer;
import pl.smarttesting.bik.score.domain.Pesel;

import static org.mockito.BDDMockito.given;

// Dotyczy lekcji 05-03
public class BikContractBaseClass {

	@BeforeEach
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(new BikController(mockedScoreAnalyzer()));
	}

	private ScoreAnalyzer mockedScoreAnalyzer() {
		ScoreAnalyzer verifier = BDDMockito.mock(ScoreAnalyzer.class);
		given(verifier.shouldGrantLoan(new Pesel("89050193724"))).willReturn(true);
		return verifier;
	}
}

