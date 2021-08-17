package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@JsonPropertyOrder({"id", "created", "pool", "length"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Code {

  private static final Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(
      name = "code_id", nullable = false, updatable = false,
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
  private Date created;

  @Column(nullable = false, updatable = false)
  private int length;

  @Column(nullable = false, updatable = false)
  @NonNull
  private String pool;

  @Column(nullable = false, updatable = false)
  @JsonIgnore
  private int poolSize;

  @Column(name = "code_text", nullable = false, updatable = false)
  @JsonIgnore
  private String text;

  @ManyToOne(fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "match_id", nullable = true, updatable = false)
  @JsonIgnore
  private Match match;

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "user_id", nullable = true, updatable = false)
  @JsonIgnore
  private User user;

  @OneToMany(mappedBy = "code", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @OrderBy("created ASC")
  @NonNull
  @JsonIgnore
  private final List<Guess> guesses = new LinkedList<>();

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

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @NonNull
  public String getPool() {
    return pool;
  }

  public void setPool(@NonNull String pool) {
    this.pool = pool;
  }

  public int getPoolSize() {
    return poolSize;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Match getMatch() {
    return match;
  }

  public void setMatch(Match match) {
    this.match = match;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @NonNull
  public List<Guess> getGuesses() {
    return guesses;
  }

  @JsonProperty(value = "text", access = Access.READ_ONLY)
  public String getSecretText() {
    String text = null;
    if (match == null
        && guesses.stream().anyMatch((guess) -> guess.getExactMatches() == length)) {
      text = this.text;
    } else if (match != null
        && match.getEnding().compareTo(new Date()) <= 0) {
      text = this.text;
    }
    return text;
  }

  @PrePersist
  private void setAdditionalFields() {
    poolSize = (int) pool
        .codePoints()
        .count();
    UUID uuid = UUID.randomUUID();
    ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    key = ENCODER.encodeToString(buffer.array());
  }

}
