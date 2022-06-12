package io.github.dzdialectapispring.adverb.adjective;

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
@RequestMapping("api/v1/adverbs")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdverbController {

  private final AdverbService adverbService;

  @GetMapping
  public Set<String> getAllAdjectivesIds() {
    return adverbService.getAllAdverbsIds();
  }

  @GetMapping("/{id}/values")
  public List<SentenceDTO> getVerbById(@PathVariable String id) {
    return adverbService.getAdjectiveValuesById(id);
  }

}
