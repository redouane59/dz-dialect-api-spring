package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContributionSentenceDTO extends WordDTO {

  private String id;
  @JsonProperty("author_id")
  private String authorId;

  public ContributionSentenceDTO(final ContributionSentence sentence) {
    super(sentence);
    this.id       = sentence.getId();
    this.authorId = sentence.getAuthorId();
  }
}
