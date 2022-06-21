package io.github.dzdialectapispring.noun;

import io.github.dzdialectapispring.sentence.WordDTO;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/nouns")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
public class NounController {

  private final NounService nounService;

  @GetMapping
  public Set<String> getAllNounsIds() {
    return nounService.getAllNounsIds();
  }

  @GetMapping("/{id}/values")
  public List<WordDTO> getNounValues(@PathVariable String id) {
    return nounService.getNounValuesById(id);
  }

}
