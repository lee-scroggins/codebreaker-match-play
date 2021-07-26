package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.Code;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodeRepository extends JpaRepository<Code, UUID> {

  Stream<Code> findAllByUserOrderByCreatedDesc(User user);

  @Query("SELECT c FROM Code AS c WHERE c.user = :user AND EXISTS("
      + "    SELECT g FROM Guess AS g WHERE g.code = c AND g.exactMatches = c.length"
      + ") ORDER BY c.created DESC")
  Stream<Code> findAllByUserAndSolvedOrderByCreatedDesc(User user);

  @Query("SELECT c FROM Code AS c WHERE c.user = :user AND NOT EXISTS("
      + "    SELECT g FROM Guess AS g WHERE g.code = c AND g.exactMatches = c.length"
      + ") ORDER BY c.created ASC")
  Stream<Code> findAllByUserAndNotSolvedOrderByCreatedAsc(User user);

}
