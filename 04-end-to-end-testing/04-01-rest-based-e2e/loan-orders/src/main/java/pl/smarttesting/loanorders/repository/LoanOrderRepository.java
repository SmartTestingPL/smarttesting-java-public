package pl.smarttesting.loanorders.repository;

import pl.smarttesting.loanorders.order.LoanOrder;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium do wykonywania operacji w MongoDB.
 */
@Repository
public interface LoanOrderRepository extends ReactiveMongoRepository<LoanOrder, String> {
}
