package pl.smarttesting.loanorders.repository;

import pl.smarttesting.loanorders.customer.Customer;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium do wykonywania operacji na klientach w MongoDB.
 */
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
