package io.github.dzdialectapispring.noun;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.NounType;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.enumerations.WordType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter

public class Noun extends AbstractWord {

  private final List<GenderedWord> values = new ArrayList<>();
  @JsonInclude(Include.NON_EMPTY)
  private       NounType           type;
  @JsonProperty("state_preposition")
  private       String             statePrepositionId;
  @JsonProperty("deplacement_preposition")
  private       String             deplacementPrepositionId;

  public Noun() {
    setWordType(WordType.NOUN);
  }

}