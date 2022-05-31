package io.github.dzdialectapispring.other.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RootTense {
  PAST("passé"),
  PRESENT("présent"),
  FUTURE("futur"),
  IMPERATIVE("impératif");
  String description;
}