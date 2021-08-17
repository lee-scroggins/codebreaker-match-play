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
@RequestMapping("/codes/{codeKey}/guesses")
public class GuessController {

  private final CodeService codeService;

  @Autowired
  public GuessController(CodeService codeService) {
    this.codeService = codeService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Guess> get(@PathVariable String codeKey, Authentication auth) {
    return codeService.getGuesses(codeKey, (User) auth.getPrincipal());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Guess> post(
      @PathVariable String codeKey, @RequestBody Guess guess, Authentication auth) {
    return codeService
        .processGuess(codeKey, guess, (User) auth.getPrincipal())
        .map((updatedGuess) -> {
          URI location = WebMvcLinkBuilder
              .linkTo(
                  WebMvcLinkBuilder
                      .methodOn(GuessController.class)
                      .get(codeKey, updatedGuess.getKey(), auth)
              )
              .toUri();
          return ResponseEntity.created(location).body(updatedGuess);
        })
        .orElseThrow();
  }

  @GetMapping(value = "/{guessKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Guess get(
      @PathVariable String codeKey, @PathVariable String guessKey, Authentication auth) {
    return codeService
        .get(codeKey, guessKey, (User) auth.getPrincipal())
        .orElseThrow();
  }

}
