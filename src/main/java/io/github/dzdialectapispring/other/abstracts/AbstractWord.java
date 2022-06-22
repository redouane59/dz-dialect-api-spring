package io.github.dzdialectapispring.other.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.WordType;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public abstract class AbstractWord implements IWord {

  private String   id;
  @JsonProperty("word_type")
  @JsonIgnore
  private WordType wordType;

  @JsonIgnore
  public Optional<GenderedWord> getWordByGenderAndSingular(Gender gender, Lang lang, boolean isSingular) {
    Optional<GenderedWord> result = getValues().stream()
                                               .map(o -> (GenderedWord) o)
                                               .filter(o -> o.getGender(lang) == gender
                                                            || o.getGender(lang) == Gender.X
                                                            || gender == Gender.X)

                                               .filter(o -> o.isSingular() == isSingular)
                                               .findAny();
    if (result.isEmpty()) {
      LOGGER.debug("no gendered word found");
      return Optional.empty();
    }
    return result;
  }

  @Override
  public String toString() {
    return "AbstractWord{" +
           "id='" + id + '\'' +
           ", wordType=" + wordType +
           '}';
  }
}
