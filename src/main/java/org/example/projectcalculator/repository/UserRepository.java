package org.example.projectcalculator.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.example.projectcalculator.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByLogin(String login);
}
