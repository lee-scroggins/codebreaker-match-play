package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.Match;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.service.MatchService;
import java.net.URI;
import java.util.UUID;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class MatchController {

  private final MatchService service;

  public MatchController(MatchService service) {
    this.service = service;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Match> post(@RequestBody Match match, Authentication auth) {
    match = service.start(match, (User) auth.getPrincipal());
    URI location = WebMvcLinkBuilder
        .linkTo(
            WebMvcLinkBuilder
                .methodOn(MatchController.class)
                .get(match.getId(), auth)
        )
        .toUri();
    return ResponseEntity.created(location).body(match);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Match get(@PathVariable UUID id, Authentication auth) {
    return service
        .get(id)
        .orElseThrow();
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id, Authentication auth) {
    service.delete(id, (User) auth.getPrincipal());
  }

}
