package io.github.dzdialectapispring.verb.conjugation;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Conjugation extends PossessiveWord {

  private Subtense subtense;

  public Conjugation(List<Translation> translations, Gender gender, boolean singular, Possession possession, Subtense subtense, int index) {
    super(translations, gender, singular, possession, index);
    this.subtense = subtense;
  }


}