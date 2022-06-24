package io.github.dzdialectapispring.adjective;

import io.github.dzdialectapispring.sentence.WordDTO;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/adjectives")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
public class AdjectiveController {

  private final AdjectiveService adjectiveService;

  @GetMapping
  public Set<String> getAllAdjectivesIds(@RequestParam(name = "include_temporal", required = false, defaultValue = "true") boolean temporal,
                                         @RequestParam(name = "include_definitive", required = false, defaultValue = "true") boolean definitive) {
    return adjectiveService.getAllAdjectivesIds(temporal, definitive);
  }

  @GetMapping("/{id}/values")
  public List<WordDTO> getAdjectiveValues(@PathVariable String id) {
    return adjectiveService.getAdjectiveValuesById(id);
  }

}
