package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.CodeRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Code;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

  private final CodeRepository repository;
  private final Random rng;

  @Autowired
  public CodeService(CodeRepository repository, Random rng) {
    this.repository = repository;
    this.rng = rng;
  }

  public void generate(Code code) {
    int[] codePoints = code
        .getPool()
        .codePoints()
        .toArray();
    int[] selection = new int[code.getLength()];
    for (int i = 0; i < selection.length; i++) {
      selection[i] = codePoints[rng.nextInt(codePoints.length)];
    }
    code.setText(new String(selection, 0, selection.length));
  }

  public Code start(Code code, User user) {
    code.setUser(user);
    generate(code);
    return repository.save(code);
  }

  public Optional<Code> get(UUID id, User user) {
    return repository
        .findById(id)
        .map((code) -> (code.getMatch() != null || code.getUser().getId().equals(user.getId()))
            ? code
            : null
        );
  }

  public void delete(UUID id, User user) {
    repository
        .findById(id)
        .map((code) -> (code.getMatch() == null && code.getUser().getId().equals(user.getId()))
            ? code
            : null
        )
        .ifPresent(repository::delete);
  }

  public Stream<Code> getUserCodes(User user) {
    return repository.findAllByUserOrderByCreatedDesc(user);
  }

  public Stream<Code> getUserSolvedCodes(User user) {
    return repository.findAllByUserAndSolvedOrderByCreatedDesc(user);
  }

  public Stream<Code> getUserNotSolvedCodes(User user) {
    return repository.findAllByUserAndNotSolvedOrderByCreatedAsc(user);
  }

  public Iterable<Guess> getGuesses(UUID codeId, User user) {
    return repository
        .findById(codeId)
        .map((code) -> (code.getMatch() != null || code.getUser().getId().equals(user.getId()))
            ? code
            : null
        )
        .map((code) -> {
          List<Guess> guesses = code.getGuesses();
          if (code.getMatch() != null) {
            guesses.removeIf((guess) -> guess.getUser().getId().equals(user.getId()));
          }
          return guesses;
        })
        .orElseThrow();
  }

  public Optional<Guess> processGuess(UUID codeId, Guess guess, User user) {
    return repository
        .findById(codeId)
        .map((code) -> (code.getMatch() != null || code.getUser().getId().equals(user.getId()))
            ? code
            : null
        )
        .map((code) -> verifyAvailability(code, user))


  }


  private Code verifyAvailability(Code code, User user) throws IllegalStateException {
    if (code.getMatch() != null && code.getMatch().getEnding().compareTo(new Date()) <= 0) {
      throw new IllegalStateException();
    }
    if (code.getGuesses()
        .stream()
        .anyMatch((guess) ->
            guess.getUser().getId().equals(user.getId())
                && guess.getExactMatches() == code.getLength())
    ) {
      throw new IllegalStateException();
    }
    return code;
  }

  private Guess processGuess(Code code, Guess guess, User user) {
    int[] guessTextPoints = guess.getText().codePoints().toArray();
    if (code.getLength() != guessTextPoints.length) {
      throw new IllegalArgumentException();
    }
    Set<Integer> pool = code
        .getPool()
        .codePoints()
        .boxed()
        .collect(Collectors.toUnmodifiableSet());
    if (guess.getText().codePoints().anyMatch((codePoint) -> !pool.contains(codePoint))) {
      throw new IllegalArgumentException();
    }
    Map<Integer, Integer> codeTextCounts = new HashMap<>();
    Map<Integer, Integer> guessTextCounts = new HashMap<>();
    int[] codeTextPoints = code.getText().codePoints().toArray();
    int exactMatches = 0;
    for (int i = 0; i < codeTextPoints.length; i++) {
      if (guessTextPoints[i] == codeTextPoints[i]) {

      } else {

      }
    }
  }

}
