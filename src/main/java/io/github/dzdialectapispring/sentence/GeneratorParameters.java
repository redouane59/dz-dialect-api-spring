package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.verb.Verb;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GeneratorParameters {

  @Builder.Default
  private int             count = 1;
  private AbstractPronoun abstractPronoun;
  private Verb            abstractVerb;
  private Tense           tense;
  private boolean         excludePositive;
  private boolean         excludeNegative;
}
