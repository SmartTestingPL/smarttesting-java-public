package pl.smarttesting.bik.score.personal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import jmh.mbr.junit5.Microbenchmark;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.BDDMockito;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

// Dotyczy lekcji 05-05
@Measurement(iterations = 10, time = 1)
@Warmup(iterations = 1, time = 1)
@Fork(4)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Microbenchmark
@Tag("performance")
public class PersonalInformationScoreEvaluationPerformanceTests {

	@Benchmark
	@Testable
	public void test(BenchmarkContext benchmarkContext) throws Exception {
		BDDAssertions.then(benchmarkContext.personalInformationScoreEvaluation.evaluate(new Pesel("12345678901"))).isEqualTo(new Score(130));
	}

	/**
	 * Stan przekazywany między testami.
	 */
	@State(Scope.Benchmark)
	public static class BenchmarkContext {

		volatile PersonalInformationClient client = BDDMockito.mock(PersonalInformationClient.class);

		volatile OccupationRepository repository = BDDMockito.mock(OccupationRepository.class);

		volatile PersonalInformationScoreEvaluation personalInformationScoreEvaluation = new PersonalInformationScoreEvaluation(client, repository);

		/**
		 * Tworzenie nowego użytkownika przy każdym uruchomieniu testu.
		 */
		@Setup
		public void setup() {
			BDDMockito.given(this.client.getPersonalInformation(BDDMockito.any())).willReturn(new PersonalInformation(PersonalInformation.Education.BASIC, 5, PersonalInformation.Occupation.LAWYER));
			BDDMockito.given(this.repository.getOccupationScores()).willReturn(Map.of(PersonalInformation.Occupation.LAWYER, new Score(100)));
		}

	}
}
