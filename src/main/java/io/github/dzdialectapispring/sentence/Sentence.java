package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.verb.Verb;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sentence extends Word {

  @JsonProperty("additional_information")
  @Schema(name = "additional_information")
  private SentenceContent         content;
  @JsonProperty("word_propositions")
  @JsonInclude(Include.NON_EMPTY)
  private Map<Lang, List<String>> randomWords = new HashMap<>();

  public Sentence(List<Translation> translations) {
    super(translations);
  }

  @Data
  @Builder
  public static class SentenceContent {

    private Verb            abstractVerb;
    private AbstractPronoun abstractPronoun;
    private AbstractWord    abstractAdverb;
    private AbstractWord    abstractQuestion;
    private AbstractWord    abstractAdjective;
    private AbstractWord    abstractNoun;
    private Subtense        subtense;
    private SentenceSchema  sentenceSchema;
    private boolean         negation;

  }

}
