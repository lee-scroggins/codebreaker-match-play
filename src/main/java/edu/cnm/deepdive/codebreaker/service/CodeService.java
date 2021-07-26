package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.CodeRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Code;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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

  public Optional<Code> get(UUID id) {
    return repository.findById(id);
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

}
