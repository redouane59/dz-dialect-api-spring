package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.concrets.Translation;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContributionSentence extends AbstractSentence {

  private String id;
  @JsonProperty("author_id")
  private String authorId;
  
  public ContributionSentence(List<Translation> translations) {
    super(translations);
  }
}
