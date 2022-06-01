package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.verb.Verb;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonSerialize(using = SentenceSerializer.class)
public class Sentence extends Word {

  private SentenceContent content;

  public Sentence(List<Translation> translations) {
    super(translations);
  }

  // used for serialization
  public JsonNode getAdditionalInformations() {
    ObjectNode node = new ObjectMapper().createObjectNode();
    if (content == null) {
      return node;
    }
    if (this.content.getTense() != null) {
      node.put("tense", this.content.getTense().name());
    }
    return node;
  }

  @Data
  @Builder
  public static class SentenceContent {

    private Verb           abstractVerb;
    private PossessiveWord pronoun;
    private AbstractWord   abstractAdverb;
    private AbstractWord   abstractQuestion;
    //  private Adjective      abstractAdjective;
    //  private Noun           abstractNoun;
    private Tense          tense;
    //   private SentenceSchema sentenceSchema;
    private boolean        negation;
    @Builder.Default
    private List<String>   randomFrWords = new ArrayList<>();
    @Builder.Default
    private List<String>   randomArWords = new ArrayList<>();
  }

}
