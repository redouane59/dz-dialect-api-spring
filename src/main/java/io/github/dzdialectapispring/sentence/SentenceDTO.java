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
public class SentenceDTO extends WordDTO {

  @JsonProperty("additionnal_information")
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = SentenceContentDTO.class)
  private final SentenceContentDTO  sentenceContent;
  @JsonProperty("word_propositions")
  @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = WordPropositionsDTO.class)
  private final WordPropositionsDTO wordPropositions;

  public SentenceDTO(Sentence sentence) {
    super(sentence);
    this.sentenceContent = new SentenceContentDTO(sentence.getContent());
    wordPropositions     = new WordPropositionsDTO(sentence.getRandomWords());
  }

  public SentenceDTO(ContributionSentence sentence) {
    super(sentence);
    this.sentenceContent = null;
    wordPropositions     = null;
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

  @NoArgsConstructor
  public static class SentenceContentDTO {

    @JsonProperty("verb")
    @JsonInclude(Include.NON_NULL)
    private String  abstractVerb;
    @JsonProperty("adverb")
    @JsonInclude(Include.NON_NULL)
    private String  abstractAdverb;
    @JsonProperty("question")
    @JsonInclude(Include.NON_NULL)
    private String  abstractQuestion;
    @JsonProperty("adjective")
    @JsonInclude(Include.NON_NULL)
    private String  abstractAdjective;
    @JsonProperty("noun")
    @JsonInclude(Include.NON_NULL)
    private String  abstractNoun;
    @JsonProperty("tense")
    @JsonInclude(Include.NON_NULL)
    private String  subtense;
    @JsonProperty("schema")
    @JsonInclude(Include.NON_NULL)
    private String  sentenceSchema;
    @JsonInclude(Include.NON_DEFAULT)
    private boolean negation;
    @JsonProperty("pronoun")
    @JsonInclude(Include.NON_NULL)
    private String  abstractPronoun;

    public SentenceContentDTO(final SentenceContent content) {
      this.abstractVerb      = (content != null && content.getAbstractVerb() != null) ? content.getAbstractVerb().getId() : null;
      this.abstractAdjective = (content != null && content.getAbstractAdjective() != null) ? content.getAbstractAdjective().getId() : null;
      this.abstractAdverb    = (content != null && content.getAbstractAdverb() != null) ? content.getAbstractAdverb().getId() : null;
      this.abstractQuestion  = (content != null && content.getAbstractQuestion() != null) ? content.getAbstractQuestion().getId() : null;
      this.abstractNoun      = (content != null && content.getAbstractNoun() != null) ? content.getAbstractNoun().getId() : null;
      this.subtense          = (content != null && content.getSubtense() != null) ? content.getSubtense().getTense().getId() : null;
      this.sentenceSchema    = (content != null && content.getSentenceSchema() != null) ? content.getSentenceSchema().getId() : null;
      this.abstractPronoun   = (content != null && content.getAbstractPronoun() != null) ? content.getAbstractPronoun().getId() : null;
      this.negation          = content != null && content.isNegation();
    }
  }
}
