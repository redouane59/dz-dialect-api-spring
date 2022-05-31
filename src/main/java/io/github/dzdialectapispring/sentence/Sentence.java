package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonSerialize(using = SentenceSerializer.class)
public class Sentence extends Word {

  public Sentence(Set<Translation> translations) {
    super(translations);
  }


}
