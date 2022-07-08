package io.github.dzdialectapispring.adverb.adjective;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.conjugation.ConjugationListDeserializer;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Adverb extends AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private final List<Word> values = new ArrayList<>();
  @JsonProperty("adverb_type")
  private       AdverbType adverbType;
  @JsonProperty("exclude_state_verbs")
  private       boolean    excludeStateVerbs; // to avoid "je suis plus tard"

  public Adverb() {
    this.setWordType(WordType.ADVERB);
  }

}