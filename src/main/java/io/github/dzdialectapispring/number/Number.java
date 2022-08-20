package io.github.dzdialectapispring.number;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.WordType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Number extends AbstractWord {

  private final List<Word> values = new ArrayList<>();
  
  public Number() {
    this.setWordType(WordType.NUMBER);
  }

}
