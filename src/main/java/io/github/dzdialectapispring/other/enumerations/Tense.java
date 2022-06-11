package io.github.dzdialectapispring.other.enumerations;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tense {
  PAST("past", "passé"),
  PRESENT("present", "présent"),
  FUTURE("future", "futur"),
  IMPERATIVE("imperative", "impératif");
  String id;
  String descriptionFr;

  public static Optional<Tense> findById(String id) {
    return Arrays.stream(values()).filter(t -> t.getId().equals(id)).findFirst();
  }

  @Override
  public String toString() {
    return "Tense{" +
           "id='" + id + '\'' +
           ", descriptionFr='" + descriptionFr + '\'' +
           '}';
  }
}