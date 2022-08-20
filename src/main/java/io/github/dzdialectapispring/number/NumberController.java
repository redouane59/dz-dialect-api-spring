package io.github.dzdialectapispring.number;

import io.github.dzdialectapispring.sentence.SentenceDTO;
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
@RequestMapping("api/v1/numbers")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
public class NumberController {

  private final NumberService numberService;

  @GetMapping
  public Set<String> getAllNumbers() {
    return numberService.getAllNumbersIds();
  }

  @GetMapping("/{id}/values")
  public List<SentenceDTO> getNumberById(@PathVariable String id) {
    return numberService.getNumberValuesById(id);
  }

  @GetMapping("/random")
  public List<SentenceDTO> getRandomNumbers(@RequestParam(required = false, defaultValue = "1") Integer count,
                                            @RequestParam(required = false, defaultValue = "0") Integer min,
                                            @RequestParam(required = false, defaultValue = "999999999") Integer max) {
    return numberService.getRandomNumbers(count, min, max);
  }

}
