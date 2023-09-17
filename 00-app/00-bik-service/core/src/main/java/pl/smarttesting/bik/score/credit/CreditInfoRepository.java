package pl.smarttesting.bik.score.credit;

import pl.smarttesting.bik.score.domain.Pesel;

interface CreditInfoRepository {

	CreditInfo findCreditInfo(Pesel pesel);

	CreditInfo saveCreditInfo(Pesel pesel, CreditInfo creditInfo);
}
