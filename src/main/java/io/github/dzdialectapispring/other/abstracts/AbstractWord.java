package io.github.dzdialectapispring.other.abstracts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.conjugation.ConjugationListDeserializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@Getter
@Setter
public class AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private List<? super Word> values = new ArrayList<>();
  @Id
  private String             id;
  @JsonProperty("word_type")
  @JsonIgnore
  private WordType           wordType;

  @JsonIgnore
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
  }

}
