package io.github.dzdialectapispring.question;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.conjugation.ConjugationListDeserializer;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class AbstractQuestion extends AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private final List<Word> values = new ArrayList<>();

  public AbstractQuestion() {
    this.setWordType(WordType.QUESTION);
  }


}
