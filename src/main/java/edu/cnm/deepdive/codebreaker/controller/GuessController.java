package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.service.CodeService;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<Guess> post(@PathVariable UUID codeId, @RequestBody Guess guess, Authentication auth) {
    return codeService
        .processGuess(codeId, guess, (User) auth.getPrincipal())
        .map((updatedGuess) -> {
          URI location = WebMvcLinkBuilder
              .linkTo(
                  WebMvcLinkBuilder
                      .methodOn(GuessController.class)
                      .get(codeId, updatedGuess.getId(), auth)
              )
              .toUri();
          return ResponseEntity.created(location).body(updatedGuess);
        })
        .orElseThrow();
  }

  @GetMapping(value = "/{guessId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Guess get(@PathVariable UUID codeId, @PathVariable UUID guessId, Authentication auth) {
    return codeService
        .get(codeId, guessId, (User) auth.getPrincipal())
        .orElseThrow();
  }

}
