package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, UUID> {

  Optional<Match> findByKey(String key);

  Stream<Match> findAllByEndingAfterOrderByEndingAsc(Date cutoff);

  Stream<Match> findAllByEndingAfterAndCodeLengthAndPoolSizeOrderByEndingAsc(
      Date cutoff, int codeLength, int poolSize);

  Stream<Match> findAllByOriginatorOrderByCreatedDesc(User originator);

  Stream<Match> findAllByParticipantsContainsOrderByEndingAsc(User participant);

  Stream<Match> findAllByParticipantsContainsAndEndingAfterOrderByEndingAsc(
      User participant, Date cutoff);

  Stream<Match> findAllByParticipantsContainsAndEndingBeforeOrderByEndingDesc(
      User participant, Date cutoff);

  Stream<Match> findAllByParticipantsNotContainsAndEndingAfterOrderByEndingAsc(
      User participant, Date cutoff);

  Stream<Match>
  findAllByParticipantsNotContainsAndEndingAfterAndCodeLengthAndPoolSizeOrderByEndingAsc(
      User participant, Date cutoff, int codeLength, int poolSize);

  // etc.

}
