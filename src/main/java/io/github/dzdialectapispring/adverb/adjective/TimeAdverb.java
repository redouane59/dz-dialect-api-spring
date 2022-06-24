package io.github.dzdialectapispring.adverb.adjective;

import io.github.dzdialectapispring.other.enumerations.Tense;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class TimeAdverb extends Adverb {

  private Tense tense;

  public TimeAdverb(Tense tense) {
    super();
    this.tense = tense;
    setAdverbType(AdverbType.TIME);
  }

}