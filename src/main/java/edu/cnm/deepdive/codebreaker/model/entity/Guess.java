package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@JsonPropertyOrder({"id", "created", "text", "exactMatches", "nearMatches"})
@JsonInclude(Include.NON_NULL)
public class Guess {

  private static final Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(
      name = "guess_id", nullable = false, updatable = false,
      columnDefinition = "CHAR(16) FOR BIT DATA"
  )
  @NonNull
  @JsonIgnore
  private UUID id;

  @Column(name = "rest_key", unique = true)
  @NonNull
  @JsonProperty(value = "id", access = Access.READ_ONLY)
  private String key;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  @NonNull
  @JsonProperty(access = Access.READ_ONLY)
  private Date created;

  @Column(name = "guess_text", nullable = false, updatable = false)
  @NonNull
  private String text;

  @Column(nullable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private int exactMatches;

  @Column(nullable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  private int nearMatches;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "code_id", nullable = false, updatable = false)
  @NonNull
  @JsonIgnore
  private Code code;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  @JsonIgnore
  private User user;

  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public String getKey() {
    return key;
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

  @JsonProperty(access = Access.READ_ONLY)
  public boolean isSolution() {
    return (exactMatches == code.getLength());
  }

  @PrePersist
  private void setAdditionalFields() {
    UUID uuid = UUID.randomUUID();
    ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    key = ENCODER.encodeToString(buffer.array());
  }

}
