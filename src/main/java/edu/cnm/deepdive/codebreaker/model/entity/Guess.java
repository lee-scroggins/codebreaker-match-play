package edu.cnm.deepdive.codebreaker.model.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
public class Guess {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(
      name = "guess_id", nullable = false, updatable = false,
      columnDefinition = "CHAR(16) FOR BIT DATA"
  )
  @NonNull
  private UUID id;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  @NonNull
  private Date created;

  @Column(name = "guess_text", nullable = false, updatable = false)
  @NonNull
  private String text;

  @Column(nullable = false, updatable = false)
  private int exactMatches;

  @Column(nullable = false, updatable = false)
  private int nearMatches;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "code_id", nullable = false, updatable = false)
  @NonNull
  private Code code;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  @NonNull
  public String getText() {
    return text;
  }

  public void setText(@NonNull String text) {
    this.text = text;
  }

  public int getExactMatches() {
    return exactMatches;
  }

  public void setExactMatches(int exactMatches) {
    this.exactMatches = exactMatches;
  }

  public int getNearMatches() {
    return nearMatches;
  }

  public void setNearMatches(int nearMatches) {
    this.nearMatches = nearMatches;
  }

  @NonNull
  public Code getCode() {
    return code;
  }

  public void setCode(@NonNull Code code) {
    this.code = code;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
