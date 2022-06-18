package io.github.dzdialectapispring.other.concrets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PossessiveWord extends GenderedWord implements Comparable {

  private Possession possession;
  @JsonInclude(Include.NON_DEFAULT)
  @Setter
  @Getter
  private int        index;

  public PossessiveWord(List<Translation> translation, Gender gender, boolean singular, Possession possession, int index) {
    super(translation, gender, singular);
    this.possession = possession;
    this.index      = index;
  }

  @Override
  public int compareTo(final Object o) {
    return (getIndex() - ((Conjugation) o).getIndex());
  }

}
