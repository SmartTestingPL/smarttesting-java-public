package pl.smarttesting.bik.score.infrastructure;

import java.util.UUID;

import pl.smarttesting.bik.score.analysis.ScoreAnalyzer;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BikController {
	
	private final ScoreAnalyzer scoreAnalyzer;

	public BikController(ScoreAnalyzer scoreAnalyzer) {
		this.scoreAnalyzer = scoreAnalyzer;
	}

	@GetMapping(value = "/{pesel}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CustomerVerificationResult> score(@PathVariable Pesel pesel) {
		if (this.scoreAnalyzer.shouldGrantLoan(pesel)) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new CustomerVerificationResult(UUID.randomUUID(), CustomerVerificationResult.Status.VERIFICATION_PASSED));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new CustomerVerificationResult(UUID.randomUUID(), CustomerVerificationResult.Status.VERIFICATION_FAILED));
	}

}
