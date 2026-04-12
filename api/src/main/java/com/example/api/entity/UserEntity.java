package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "password_salt", nullable = false)
  private String passwordSalt;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getPasswordSalt() {
    return passwordSalt;
  }

  public void setPasswordSalt(String passwordSalt) {
    this.passwordSalt = passwordSalt;
  }
}
