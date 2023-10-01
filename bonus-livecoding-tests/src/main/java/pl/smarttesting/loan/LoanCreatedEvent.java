package pl.smarttesting.loan;

import pl.smarttesting.event.Event;

import java.util.UUID;

public class LoanCreatedEvent extends Event {

	private final UUID loanUuid;

	public LoanCreatedEvent(UUID uuid) {
		super();
		loanUuid = uuid;
	}

	public UUID getLoanUuid() {
		return loanUuid;
	}

}
