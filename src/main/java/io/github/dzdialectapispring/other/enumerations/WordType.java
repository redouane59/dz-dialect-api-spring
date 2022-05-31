package io.github.dzdialectapispring.other.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WordType {

  VERB("verb"),
  ADJECTIVE("adjective"),
  NOUN("noun"),
  ADVERB("adverb"),
  QUESTION("question"),
  PRONOUN("pronoun"),
  ARTICLE("article"),
  SUFFIX("suffix"),
  PREPOSITION("preposition"); // à la / au

  private String value;

}