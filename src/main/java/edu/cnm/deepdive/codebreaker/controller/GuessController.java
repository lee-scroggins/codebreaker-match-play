package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.service.CodeService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/codes/{codeId}/guesses")
public class GuessController {

  private final CodeService codeService;
  // TODO Declare a GuessService field.

  @Autowired
  public GuessController(CodeService codeService) {
    this.codeService = codeService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Guess> get(@PathVariable UUID codeId, Authentication auth) {
    return codeService.getGuesses(codeId, (User) auth.getPrincipal());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Guess> post(@PathVariable UUID codeId, Guess guess, Authentication auth) {
    // TODO Invoke method in codeService & return result as a ResponseEntity constructed from the Guess object.
  }

}
