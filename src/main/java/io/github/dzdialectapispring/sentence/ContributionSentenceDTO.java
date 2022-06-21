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
  @JsonProperty("thumb_up_count")
  private int    thumbUpCount;
  @JsonProperty("thumb_down_count")
  private int    thumbDownCount;

  public ContributionSentenceDTO(final ContributionSentence sentence) {
    super(sentence);
    this.id             = sentence.getId();
    this.authorId       = sentence.getAuthorId();
    this.thumbUpCount   = sentence.getThumbUpCount();
    this.thumbDownCount = sentence.getThumbDownCount();
  }
}
