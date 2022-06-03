package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.verb.Verb;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@AllArgsConstructor
@NoArgsConstructor
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
    if (this.content.getSubtense() != null) {
      node.put("tense", this.content.getSubtense().getTense().getId());
    }
    if (this.content.getAbstractVerb() != null) {
      node.put("verb", this.content.getAbstractVerb().getId());
    }
    if (this.content.getAbstractPronoun() != null) {
      node.put("pronoun", this.content.getAbstractPronoun().getId());
    }
    return node;
  }

  @Data
  @Builder
  public static class SentenceContent {

    private Verb                                  abstractVerb;
    private AbstractWord                          abstractPronoun;
    private AbstractWord                          abstractAdverb;
    private AbstractWord                          abstractQuestion;
    //  private Adjective      abstractAdjective;
    //  private Noun           abstractNoun;
    private Subtense                              subtense;
    private SentenceSchema                        sentenceSchema;
    private boolean                               negation;
    @Builder.Default
    private Map<Lang, List<Pair<String, String>>> randomWords = new HashMap<>();
  }

}
