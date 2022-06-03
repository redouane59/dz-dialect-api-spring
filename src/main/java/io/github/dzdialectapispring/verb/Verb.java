package io.github.dzdialectapispring.verb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.conjugation.Conjugation;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.RootTense;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_DEFAULT)
// @todo reflexive verbs (ça me plaît, il me faut, etc.)
public class Verb extends AbstractWord {

  @JsonProperty("verb_type")
  private VerbType verbType;

  public Optional<Conjugation> getConjugationByGenderSingularPossessionAndTense(Gender gender,
                                                                                boolean isSingular,
                                                                                Possession possession,
                                                                                RootTense tense) {
    return getValues().stream().map(o -> (Conjugation) o)
                      .filter(o -> o.getTense().getRootTense() == tense)
                      .filter(o -> o.isSingular() == isSingular)
                      .filter(o -> o.getPossession() == possession)
                      .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                      .findAny();

  }
}