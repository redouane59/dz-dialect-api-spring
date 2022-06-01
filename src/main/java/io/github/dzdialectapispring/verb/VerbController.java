package io.github.dzdialectapispring.verb;

import io.github.dzdialectapispring.other.enumerations.RootTense;
import io.github.dzdialectapispring.sentence.Sentence;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/verbs")
@AllArgsConstructor
public class VerbController {

  private final VerbService verbService;

  @GetMapping
  public Set<String> getAllVerbIds() {
    return verbService.getAllVerbIds();
  }

  @GetMapping("/{id}/values")
  public List<Sentence> getVerbById(@PathVariable String id, @RequestParam(required = false) RootTense tense) {
    return verbService.getVerbConjugationsById(id, tense);
  }


}
