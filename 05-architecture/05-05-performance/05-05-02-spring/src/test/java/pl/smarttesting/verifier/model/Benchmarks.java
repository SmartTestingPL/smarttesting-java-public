/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

/**
 * Microbenchmark dla {@link CustomerVerifier}.
 *
 * Measurement - 2 iteracje pomiarowe, 10 sekund każda
 * Warmup - 2 iteracje rozgrzewające JVM, 10 sekund każda
 * Fork - liczba powtórzeń pomiaru
 * BenchmarkMode - sposób pomiaru
 * OutputTimeUnit - jednostka wyniku
 * Threads - liczba wątków (domyślnie tyle co CPU)
 */
@Measurement(iterations = 2)
@Warmup(iterations = 2)
@Fork(1)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(Threads.MAX)
public class Benchmarks {

	/**
	 * Test micro-benchmarkowy. Sprawdza jak szybki jest algorytm weryfikujący czy klient
	 * jest oszustem.
	 *
	 * @param context - kontekst przekazywany między testami
	 */
	@Benchmark
	public void shouldProcessFraud(BenchmarkContext context) {
		CustomerVerifier customerVerifier = new CustomerVerifier(BDDMockito.mock(BIKVerificationService.class), Collections.emptySet(), BDDMockito.mock(FraudAlertNotifier.class), BDDMockito.mock(VerificationRepository.class));

		customerVerifier.verify(context.customer);
	}


	/**
	 * Stan przekazywany między testami.
	 */
	@State(Scope.Benchmark)
	public static class BenchmarkContext {

		volatile Customer customer;

		/**
		 * Tworzenie nowego użytkownika przy każdym uruchomieniu testu.
		 */
		@Setup
		public void setup() {
			this.customer = new Customer(UUID.randomUUID(), fraud());
		}

		private Person fraud() {
			return new Person("Fraud", "Fraudowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
		}

	}
}
