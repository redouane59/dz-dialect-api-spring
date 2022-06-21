package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AbstractSentence extends Word {

  @JsonProperty("thumb_up_count")
  private int thumbUpCount   = 0;
  @JsonProperty("thumb_down_count")
  private int thumbDownCount = 0;

  public AbstractSentence(List<Translation> translations) {
    super(translations);
  }

  public void incrementThumb(boolean up) {
    if (up) {
      thumbUpCount++;
    } else {
      thumbDownCount++;
    }
  }

}
