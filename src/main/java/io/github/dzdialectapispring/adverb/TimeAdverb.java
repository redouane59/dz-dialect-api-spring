package io.github.dzdialectapispring.adverb;

import io.github.dzdialectapispring.other.enumerations.Tense;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeAdverb extends Adverb {

  private Tense tense;

  public TimeAdverb(Tense tense) {
    super();
    this.tense = tense;
    setAdverbType(AdverbType.TIME);
  }

}