package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

import pl.smarttesting.bik.score.credit.CreditInfo.DebtPaymentHistory;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Dane testowe do bazy danych - ułatwienie dla uczestników szkolenia
 *
 */
@ChangeUnit(id = "myChangeUnitId", order = "001")
public class DatabaseChangelog {
	private final MongoTemplate template;

	public DatabaseChangelog(MongoTemplate template) {
		this.template = template;
	}

	@BeforeExecution
	public void before() {
		template.createCollection("creditInfoDocuments");
	}

	@RollbackBeforeExecution
	public void rollbackBefore() {
		template.dropCollection("creditInfoDocuments");
	}

	@Execution
	public void migrationMethod() {
		template.save(creditInfo1());
		template.save(creditInfo2());
		template.save(creditInfo3());
		template.save(creditInfo4());
		template.save(creditInfo5());
	}

	@RollbackExecution
	public void rollback() {
		template.remove(creditInfo1());
		template.remove(creditInfo2());
		template.remove(creditInfo3());
		template.remove(creditInfo4());
		template.remove(creditInfo5());
	}

	private CreditInfoDocument creditInfo1() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("100"), new BigDecimal("200"), DebtPaymentHistory.NOT_A_SINGLE_UNPAID_INSTALLMENT);
		Pesel pesel = new Pesel("89050193724");
		return new CreditInfoDocument(creditInfo, pesel);
	}
	
	private CreditInfoDocument creditInfo2() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("500"), new BigDecimal("1000"), DebtPaymentHistory.INDIVIDUAL_UNPAID_INSTALLMENTS);
		Pesel pesel = new Pesel("56020172634");
		return new CreditInfoDocument(creditInfo, pesel);
	}
	
	private CreditInfoDocument creditInfo3() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("1000"), new BigDecimal("2000"), DebtPaymentHistory.INDIVIDUAL_UNPAID_INSTALLMENTS);
		Pesel pesel = new Pesel("79061573376");
		return new CreditInfoDocument(creditInfo, pesel);
	}
	
	private CreditInfoDocument creditInfo4() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5000"), new BigDecimal("7000"), DebtPaymentHistory.MULTIPLE_UNPAID_INSTALLMENTS);
		Pesel pesel = new Pesel("64091148892");
		return new CreditInfoDocument(creditInfo, pesel);
	}
	
	private CreditInfoDocument creditInfo5() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("10000"), new BigDecimal("20000"), DebtPaymentHistory.NOT_A_SINGLE_PAID_INSTALLMENT);
		Pesel pesel = new Pesel("63081514479");
		return new CreditInfoDocument(creditInfo, pesel);
	}
}
