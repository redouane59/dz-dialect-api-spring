package io.github.dzdialectapispring.verb;

import lombok.Getter;

@Getter
public enum VerbType {

  STATE,
  DEPLACEMENT,
  ACTION,
  POSSESSION,
  DOUBLE // @to manage verb + verb

}
