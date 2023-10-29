package pl.smarttesting.loanorders.repository;

import pl.smarttesting.loanorders.order.LoanOrder;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium do wykonywania operacji na  w MongoDB.
 */
@Repository
public interface LoanOrderRepository extends MongoRepository<LoanOrder, String> {
}
