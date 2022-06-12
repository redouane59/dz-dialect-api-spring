package io.github.dzdialectapispring.adjective;

import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/adjectives")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdjectiveController {

  private final AdjectiveService adjectiveService;

  @GetMapping
  public Set<String> getAllAdjectivesIds() {
    return adjectiveService.getAllAdjectivesIds();
  }

  @GetMapping("/{id}/values")
  public List<SentenceDTO> getVerbById(@PathVariable String id) {
    return adjectiveService.getAdjectiveValuesById(id);
  }

}
