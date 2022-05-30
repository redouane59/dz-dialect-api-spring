package io.github.dzdialectapispring.sentence;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Sentence {

  private Set<Translation> translations = new HashSet<>();


}
