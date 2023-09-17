package pl.smarttesting.bik.score.credit;

import pl.smarttesting.bik.score.domain.Pesel;

interface CreditInfoListener {

	void storeCreditInfo(Pesel pesel, CreditInfo creditInfo);
}
