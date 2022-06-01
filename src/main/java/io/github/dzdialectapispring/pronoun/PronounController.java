package io.github.dzdialectapispring.pronoun;

import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/pronouns")
@AllArgsConstructor
public class PronounController {

  private final PronounService pronounService;

  @GetMapping
  public PossessiveWord getPronoun(@RequestParam Gender gender, @RequestParam boolean isSingular, @RequestParam Possession possession) {
    return pronounService.getPronoun(gender, isSingular, possession);
  }
}
