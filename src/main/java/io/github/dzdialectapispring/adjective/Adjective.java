package io.github.dzdialectapispring.adjective;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.NounType;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.conjugation.ConjugationListDeserializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Adjective extends AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private final List<GenderedWord> values = new ArrayList<>();
  @JsonProperty("possible_nouns")
  List<NounType> possibleNouns = new ArrayList<>();
  private boolean temporal;
  private boolean definitive;

  public Adjective() {
    setWordType(WordType.ADJECTIVE);
  }


  public Optional<GenderedWord> getAdjective(Adjective adjective, PossessiveWord subject, Lang lang) {
    if (subject == null) {
      System.out.println("null subject");
      return Optional.empty();
    }
    return adjective.getWordByGenderAndSingular(subject.getGender(lang), lang, subject.isSingular());
  }

  public void importConfig(final Adjective adjective) {
    this.possibleNouns = adjective.getPossibleNouns();
    this.temporal      = adjective.isTemporal();
    this.definitive    = adjective.isDefinitive();
  }
}