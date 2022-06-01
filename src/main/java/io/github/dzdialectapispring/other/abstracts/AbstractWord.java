package io.github.dzdialectapispring.other.abstracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.conjugation.ConjugationListDeserializer;
import io.github.dzdialectapispring.other.enumerations.WordType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private List<? super Word> values = new ArrayList<>();
  private String             id;
  @JsonProperty("word_type")
  private WordType           wordType;

}
