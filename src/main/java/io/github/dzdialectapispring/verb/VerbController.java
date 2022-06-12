package io.github.dzdialectapispring.verb;

import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/verbs")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
public class VerbController {

  private final VerbService verbService;

  @GetMapping
  public Set<String> getAllVerbIds() throws ExecutionException, InterruptedException {
    return verbService.getAllVerbIds();
  }

  @GetMapping("/{id}/tenses")
  public Set<String> getAvailableTenses(@PathVariable String id) throws ExecutionException, InterruptedException {
    return verbService.getAvailableTenses(id);
  }

  @GetMapping("/{id}/values")
  public List<SentenceDTO> getVerbById(@PathVariable String id, @RequestParam(required = false, name = "tense") String tenseId)
  throws ExecutionException, InterruptedException {
    return verbService.getVerbConjugationsById(id, tenseId);
  }


}
