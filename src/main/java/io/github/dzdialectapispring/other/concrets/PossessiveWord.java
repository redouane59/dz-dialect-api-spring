package io.github.dzdialectapispring.other.concrets;

import io.github.dzdialectapispring.other.enumerations.Gender;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PossessiveWord extends GenderedWord {

  private Possession possession;

  public PossessiveWord(Set<Translation> translation, Gender gender, boolean singular, Possession possession) {
    super(translation, gender, singular);
    this.possession = possession;
  }

  @Deprecated
  public PossessiveWord(GenderedWord genderedWord) {
    super(genderedWord.getGender(), genderedWord.isSingular());
    this.possession = Possession.OTHER;
    this.setTranslations(genderedWord.getTranslations());
  }
}
