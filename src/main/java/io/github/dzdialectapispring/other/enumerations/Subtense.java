package io.github.dzdialectapispring.other.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// @todo add root tense
public enum Subtense {
  PAST_AVOIR(Tense.PAST),
  PAST_ETRE(Tense.PAST), // @todo dev verbs
  PAST_IMPARFAIT(Tense.PAST), // @todo dev verbs
  PRESENT(Tense.PRESENT),
  FUTURE(Tense.FUTURE),
  IMPERATIVE(Tense.IMPERATIVE);

  Tense tense;
}