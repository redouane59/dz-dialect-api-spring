package io.github.dzdialectapispring.other.enumerations;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RootLang {
  FR("français"),
  AR("arabe");

  final String value;
}