package io.github.dzdialectapispring.verb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
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
                                                                                Tense tense) {
    return getValues().stream().map(o -> (Conjugation) o)
                      .filter(o -> o.getSubtense().getTense() == tense)
                      .filter(o -> o.isSingular() == isSingular)
                      .filter(o -> o.getPossession() == possession)
                      .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                      .findAny();

  }

  public Conjugation getVerbConjugation(Verb verb, PossessiveWord subject, Subtense tense, Lang lang) {

/*    if (subject == null) {
      subject = pronounService.getRandomPronoun();
      tense   = Tense.IMPERATIVE;
    }*/
    Optional<Conjugation>
        conjugation =
        verb.getConjugationByGenderSingularPossessionAndTense(subject.getGender(lang),
                                                              subject.isSingular(),
                                                              subject.getPossession(),
                                                              tense.getTense());
    if (conjugation.isEmpty()) {
      System.err.println("no conjugation found for");
      return null;
    }
    return conjugation.get();
  }
}