package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import java.util.List;
import java.util.Map;

public class SentenceDTO {

  @JsonProperty("dz")
  private final String              dz;
  @JsonProperty("dz_ar")
  private final String              dzAr;
  @JsonProperty("fr")
  private final String              fr;
  @JsonProperty("additionnal_information")
  private final SentenceContentDTO  sentenceContent;
  @JsonProperty("word_propositions")
  private final WordPropositionsDTO wordPropositions;

  public SentenceDTO(Sentence sentence) {
    this.dz              = sentence.getDzTranslation();
    this.dzAr            = sentence.getDzTranslationAr();
    this.fr              = sentence.getFrTranslation();
    this.sentenceContent = new SentenceContentDTO(sentence.getContent());
    wordPropositions     = new WordPropositionsDTO(sentence.getRandomWords());
  }

  public static class WordPropositionsDTO {

    @JsonProperty("dz")
    private final List<String> dz;
    @JsonProperty("dz_ar")
    private final List<String> dzAr;
    @JsonProperty("fr")
    private final List<String> fr;

    public WordPropositionsDTO(final Map<Lang, List<String>> randomWords) {
      this.dz   = randomWords.get("dz");
      this.dzAr = randomWords.get("dz_Ar");
      this.fr   = randomWords.get("fr");
    }
  }

  public static class SentenceContentDTO {

    @JsonProperty("verb")
    private final String  abstractVerb;
    @JsonProperty("adverb")
    private final String  abstractAdverb;
    @JsonProperty("question")
    private final String  abstractQuestion;
    @JsonProperty("adjective")
    private final String  abstractAdjective;
    @JsonProperty("noun")
    private final String  abstractNoun;
    @JsonProperty("tense")
    private final String  subtense;
    @JsonProperty("schema")
    private final String  sentenceSchema;
    private final boolean negation;
    @JsonProperty("pronoun")
    private final String  abstractPronoun;

    public SentenceContentDTO(final SentenceContent content) {
      this.abstractVerb      = content.getAbstractVerb() != null ? content.getAbstractVerb().getId() : null;
      this.abstractAdjective = content.getAbstractAdjective() != null ? content.getAbstractAdjective().getId() : null;
      this.abstractAdverb    = content.getAbstractAdverb() != null ? content.getAbstractAdverb().getId() : null;
      this.abstractQuestion  = content.getAbstractQuestion() != null ? content.getAbstractQuestion().getId() : null;
      this.abstractNoun      = content.getAbstractNoun() != null ? content.getAbstractNoun().getId() : null;
      this.subtense          = content.getSubtense() != null ? content.getSubtense().getTense().getId() : null;
      this.sentenceSchema    = content.getSentenceSchema() != null ? content.getSentenceSchema().getId() : null;
      this.abstractPronoun   = content.getAbstractPronoun() != null ? content.getAbstractPronoun().getId() : null;
      this.negation          = content.isNegation();
    }
  }
}
