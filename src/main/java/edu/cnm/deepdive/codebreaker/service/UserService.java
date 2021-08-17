package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.UserRepository;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository repository;

  @Autowired
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public Optional<User> get(UUID id) {
    return repository.findById(id);
  }

  public Optional<User> get(String key) {
    return repository.findByKey(key);
  }

  public User save(User user) {
    return repository.save(user);
  }

  public User getOrCreate(String oauthKey, String displayName) {
    return repository
        .save(
            repository
                .findByOauthKey(oauthKey)
                .map((user) -> {
                  // TODO Check is user is inactive.
                  user.setConnected(new Date());
                  return user;
                })
                .orElseGet(() -> {
                  User user = new User();
                  user.setOauthKey(oauthKey);
                  user.setDisplayName(displayName);
                  user.setConnected(new Date());
                  return user;
                })
        );
  }

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    Collection<SimpleGrantedAuthority> grants =
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(
        getOrCreate(jwt.getSubject(), jwt.getClaimAsString("name")),
        jwt.getTokenValue(),
        grants
    );
  }

}
