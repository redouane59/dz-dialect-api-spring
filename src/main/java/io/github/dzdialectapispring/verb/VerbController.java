package io.github.dzdialectapispring.verb;

import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.conjugation.Conjugation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public List<Sentence> getVerbById(@PathVariable String id) {
    Verb              verb         = verbService.getVerbById(id);
    List<Conjugation> conjugations = verb.getValues().stream().map(v -> (Conjugation) v).collect(Collectors.toList());
    List<Sentence>    result       = new ArrayList();
    for (Conjugation conjugation : conjugations) {
      String frValue   = conjugation.getFrTranslation();
      String dzValue   = conjugation.getDzTranslation();
      String dzValueAr = conjugation.getDzTranslationAr();
      result.add(new Sentence(List.of(new Translation(Lang.FR, frValue), new Translation(Lang.DZ, dzValue, dzValueAr))));
    }
    return result;
  }


}
