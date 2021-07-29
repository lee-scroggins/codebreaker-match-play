package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.MatchRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Code;
import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

  private final MatchRepository repository;
  private final CodeService codeService;

  @Autowired
  public MatchService(MatchRepository repository, CodeService codeService) {
    this.repository = repository;
    this.codeService = codeService;
  }

  public Match start(Match match, User user) {
    match.setOriginator(user);
    match.getParticipants().add(user);
    List<Code> codes = match.getCodes();
    for (int i = 0; i < match.getCodesToGenerate(); i++) {
      Code code = new Code();
      code.setLength(match.getCodeLength());
      code.setPool(match.getPool());
      code.setMatch(match);
      codeService.generate(code);
      codes.add(code);
    }
    return repository.save(match);
  }

  public Optional<Match> get(UUID id) {
    return repository.findById(id);
  }

  public Stream<Match> getAvailableMatches(User user, Date cutoff, int codeLength, int poolSize) {
    return repository
        .findAllByParticipantsNotContainsAndEndingAfterAndCodeLengthAndPoolSizeOrderByEndingAsc(
            user, cutoff, codeLength, poolSize
        );
  }

  public Stream<Match> getUserMatchesAfterCutoff(User user, Date cutoff) {
    return repository.findAllByParticipantsContainsAndEndingAfterOrderByEndingAsc(user, cutoff);
  }

  public Stream<Match> getUserMatchesBeforeCutoff(User user, Date cutoff) {
    return repository.findAllByParticipantsContainsAndEndingBeforeOrderByEndingDesc(user, cutoff);
  }


  public void delete(UUID id, User user) {
    repository
        .findById(id)
        .map((match) -> (match.getOriginator().getId().equals(user.getId())) ? match : null)
        .ifPresent(repository::delete);
  }

}
