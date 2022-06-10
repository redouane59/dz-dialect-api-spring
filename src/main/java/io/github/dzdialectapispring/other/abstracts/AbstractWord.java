package io.github.dzdialectapispring.other.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.enumerations.WordType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class AbstractWord implements IWord {

  private String   id;
  @JsonProperty("word_type")
  @JsonIgnore
  private WordType wordType;

/*  @JsonIgnore
  public Optional<? extends GenderedWord> getWordByGenderAndSingular(Gender gender, Lang lang, boolean isSingular) {
    Optional<? extends GenderedWord> result = getValues().stream()
                                                         .map(o -> (GenderedWord) o)
                                                         .filter(o -> o.getGender(lang) == gender
                                                                      || o.getGender(lang) == Gender.X
                                                                      || gender == Gender.X)
                                                         .filter(o -> o.isSingular() == isSingular)
                                                         .findAny();
    if (result.isEmpty()) {
      System.err.println("no word found");
      return Optional.empty();
    }
    return result;
  }*/

}
