package io.github.dzdialectapispring.other.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// @todo add root tense
public enum Tense {
  PAST_AVOIR(RootTense.PAST),
  PAST_ETRE(RootTense.PAST), // @todo dev verbs
  PRESENT(RootTense.PRESENT),
  FUTURE(RootTense.FUTURE),
  IMPERATIVE(RootTense.IMPERATIVE);

  RootTense rootTense;
}