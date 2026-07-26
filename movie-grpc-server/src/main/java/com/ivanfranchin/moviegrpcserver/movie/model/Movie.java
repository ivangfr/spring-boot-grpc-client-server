package com.ivanfranchin.moviegrpcserver.movie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "movies")
public class Movie {

  @Id private String imdbId;

  private String title;
  private Integer year;

  @Enumerated(EnumType.STRING)
  private Genre genre;
}
