package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.verb.Verb;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GeneratorParameters {

  @Builder.Default
  private int             count = 1;
  private AbstractPronoun abstractPronoun;
  private Verb            abstractVerb;
  private Tense           tense;
}
