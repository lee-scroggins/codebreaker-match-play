package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.CodeRepository;
import edu.cnm.deepdive.codebreaker.model.dao.GuessRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

  private final CodeRepository codeRepository;
  private final GuessRepository guessRepository;
  private final Random rng;

  @Autowired
  public CodeService(
      CodeRepository codeRepository, GuessRepository guessRepository, Random rng) {
    this.codeRepository = codeRepository;
    this.guessRepository = guessRepository;
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
    return codeRepository.save(code);
  }

  public Optional<Code> get(String key, User user) {
    return codeRepository
        .findByKey(key)
        .map((code) -> (code.getMatch() != null || code.getUser().getId().equals(user.getId()))
            ? code
            : null
        );
  }

  public void delete(String key, User user) {
    codeRepository
        .findByKey(key)
        .map((code) -> (code.getMatch() == null && code.getUser().getId().equals(user.getId()))
            ? code
            : null
        )
        .ifPresent(codeRepository::delete);
  }

  public Stream<Code> getUserCodes(User user) {
    return codeRepository.findAllByUserOrderByCreatedDesc(user);
  }

  public Stream<Code> getUserSolvedCodes(User user) {
    return codeRepository.findAllByUserAndSolvedOrderByCreatedDesc(user);
  }

  public Stream<Code> getUserNotSolvedCodes(User user) {
    return codeRepository.findAllByUserAndNotSolvedOrderByCreatedAsc(user);
  }

  public Iterable<Guess> getGuesses(String key, User user) {
    return codeRepository
        .findByKey(key)
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

  public Optional<Guess> processGuess(String key, Guess guess, User user) {
    return codeRepository
        .findByKey(key)
        .map((code) -> (code.getMatch() != null || code.getUser().getId().equals(user.getId()))
            ? code
            : null
        )
        .map((code) -> verifyAvailability(code, user))
        .map((code) -> validateGuess(code, guess))
        .map((code) -> process(code, guess, user))
        .map(guessRepository::save);
  }

  public Optional<Guess> get(String codeKey, String guessKey, User user) {
    return guessRepository
        .findByKey(guessKey)
        .map((guess) -> (guess.getCode().getKey().equals(codeKey)
            && guess.getUser().getId().equals(user.getId()))
            ? guess
            : null
        );
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

  private Guess process(Code code, Guess guess, User user) {
    Map<Integer, Integer> codeTextCounts = new HashMap<>();
    Map<Integer, Integer> guessTextCounts = new HashMap<>();
    int[] codeTextPoints = code.getText().codePoints().toArray();
    int[] guessTextPoints = guess.getText().codePoints().toArray();
    int exactMatches = 0;
    for (int i = 0; i < codeTextPoints.length; i++) {
      if (guessTextPoints[i] == codeTextPoints[i]) {
        exactMatches++;
      } else {
        codeTextCounts.put(
            codeTextPoints[i], codeTextCounts.getOrDefault(codeTextPoints[i], 0) + 1);
        guessTextCounts.put(
            guessTextPoints[i], guessTextCounts.getOrDefault(guessTextPoints[i], 0) + 1);
      }
    }
    int nearMatches = guessTextCounts
        .entrySet()
        .stream()
        .filter((entry) -> codeTextCounts.containsKey(entry.getKey()))
        .mapToInt((entry) -> Math.min(entry.getValue(), codeTextCounts.get(entry.getKey())))
        .sum();
    guess.setCode(code);
    guess.setUser(user);
    guess.setExactMatches(exactMatches);
    guess.setNearMatches(nearMatches);
    code.getGuesses().add(guess);
    return guess;
  }

  private Code validateGuess(Code code, Guess guess) throws IllegalArgumentException {
    if (code.getLength() != guess.getText().codePoints().count()) {
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
    return code;
  }

}
