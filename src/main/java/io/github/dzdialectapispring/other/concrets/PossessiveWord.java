package io.github.dzdialectapispring.other.concrets;

import io.github.dzdialectapispring.other.enumerations.Gender;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PossessiveWord extends GenderedWord {

  private Possession possession;

  public PossessiveWord(List<Translation> translation, Gender gender, boolean singular, Possession possession, int index) {
    super(translation, gender, singular, index);
    this.possession = possession;
  }

}
