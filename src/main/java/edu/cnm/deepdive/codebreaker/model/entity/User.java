package edu.cnm.deepdive.codebreaker.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    name = "user_profile"
)
public class User {

  private static final Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(
      name = "user_id", nullable = false, updatable = false,
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

  @Column(nullable = false, updatable = false, unique = true)
  @NonNull
  @JsonIgnore
  private String oauthKey;

  @Column(nullable = false, unique = true)
  @NonNull
  private String displayName;

  @Column(nullable = false)
  private boolean inactive;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = true)
  @NonNull
  @JsonIgnore
  private Date connected;

  @OneToMany(mappedBy = "originator", fetch = FetchType.LAZY,
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @OrderBy("created DESC")
  @NonNull
  @JsonIgnore
  private final List<Match> matchesOriginated = new LinkedList<>();

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "participants")
  @OrderBy("created DESC")
  @NonNull
  @JsonIgnore
  private final List<Match> matches = new LinkedList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @OrderBy("created DESC")
  @NonNull
  @JsonIgnore
  private final List<Code> codes = new LinkedList<>();

  // TODO Consider whether adding the one-to-many for guesses makes sense here.

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
  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(@NonNull String oauthKey) {
    this.oauthKey = oauthKey;
  }

  @NonNull
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(@NonNull String displayName) {
    this.displayName = displayName;
  }

  public boolean isInactive() {
    return inactive;
  }

  public void setInactive(boolean inactive) {
    this.inactive = inactive;
  }

  @NonNull
  public Date getConnected() {
    return connected;
  }

  public void setConnected(@NonNull Date connected) {
    this.connected = connected;
  }

  @NonNull
  public List<Match> getMatchesOriginated() {
    return matchesOriginated;
  }

  @NonNull
  public List<Match> getMatches() {
    return matches;
  }

  @NonNull
  public List<Code> getCodes() {
    return codes;
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
