package pl.smarttesting.bik.score.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Olga Maciaszek-Sharma
 */
class PeselTests {

	@Test
	void shouldCreateNewPesel() {
		String peselString = "91121345678";

		Pesel pesel = new Pesel(peselString);

		assertThat(pesel.getPesel()).isEqualTo(peselString);
	}

	@ParameterizedTest(name = "Should throw exception when PESEL String = {0}")
	@ValueSource(strings = {"9112134567", "911213456789"})
	void shouldThrowExceptionIfPeselLengthNotEqualToEleven(String peselString) {
		assertThatIllegalArgumentException().isThrownBy(
			() -> new Pesel(peselString)
		);
	}

}
