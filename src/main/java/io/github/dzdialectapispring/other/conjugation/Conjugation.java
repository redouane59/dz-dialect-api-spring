package io.github.dzdialectapispring.other.conjugation;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Tense;
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

  private Tense tense;
  private int   index;

  public Conjugation(List<Translation> translations, Gender gender, boolean singular, Possession possession, Tense tense, int index) {
    super(translations, gender, singular, possession);
    this.tense = tense;
    this.index = index;
  }

}