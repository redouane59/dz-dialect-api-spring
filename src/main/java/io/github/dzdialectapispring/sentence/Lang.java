package io.github.dzdialectapispring.sentence;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Lang {
  FR("Français (France)"),
  DZ("Dardja (Alger)");
  // add other dialects here

  final String   value;
}
