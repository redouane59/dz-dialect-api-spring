package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = WordPropositionsDTO.class)
  private final WordPropositionsDTO wordPropositions;

  public SentenceDTO(Sentence sentence) {
    this.dz              = sentence.getDzTranslationValue();
    this.dzAr            = sentence.getDzTranslationValueAr();
    this.fr              = sentence.getFrTranslationValue();
    this.sentenceContent = new SentenceContentDTO(sentence.getContent());
    wordPropositions     = new WordPropositionsDTO(sentence.getRandomWords());
  }

  @NoArgsConstructor
  @Getter
  public static class WordPropositionsDTO {

    @JsonProperty("dz")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> dz   = new ArrayList<>();
    @JsonProperty("dz_ar")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> dzAr = new ArrayList<>();
    @JsonProperty("fr")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> fr   = new ArrayList<>();

    public WordPropositionsDTO(final Map<Lang, List<String>> randomWords) {
      this.dz   = randomWords.get(Lang.DZ);
      this.dzAr = randomWords.get(Lang.DZ + "_ar");
      this.fr   = randomWords.get(Lang.FR);
    }

    // to manage empty serialization
    @Override
    public boolean equals(Object other) {
      if (!(other instanceof WordPropositionsDTO)) {
        return false;
      }
      WordPropositionsDTO prop2 = (WordPropositionsDTO) other;
      return (this.dz.equals(prop2.getDz()) && this.fr.equals(prop2.getFr())
              || (this.dz.size() == 0 && prop2.getDz() == null && this.fr.size() == 0 && prop2.getFr() == null));
    }
  }

  public static class SentenceContentDTO {

    @JsonProperty("verb")
    @JsonInclude(Include.NON_NULL)
    private final String  abstractVerb;
    @JsonProperty("adverb")
    @JsonInclude(Include.NON_NULL)
    private final String  abstractAdverb;
    @JsonProperty("question")
    @JsonInclude(Include.NON_NULL)
    private final String  abstractQuestion;
    @JsonProperty("adjective")
    @JsonInclude(Include.NON_NULL)
    private final String  abstractAdjective;
    @JsonProperty("noun")
    @JsonInclude(Include.NON_NULL)
    private final String  abstractNoun;
    @JsonProperty("tense")
    @JsonInclude(Include.NON_NULL)
    private final String  subtense;
    @JsonProperty("schema")
    @JsonInclude(Include.NON_NULL)
    private final String  sentenceSchema;
    private final boolean negation;
    @JsonProperty("pronoun")
    @JsonInclude(Include.NON_NULL)
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
