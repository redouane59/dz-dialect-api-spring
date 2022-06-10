package io.github.dzdialectapispring.other.abstracts;

import io.github.dzdialectapispring.other.concrets.Word;
import java.util.List;

public interface IWord {

  List<? extends Word> getValues();
}
