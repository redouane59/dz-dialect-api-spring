package io.github.dzdialectapispring.other;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Preposition extends AbstractWord {

  List<GenderedWord> values = new ArrayList<>();
}
