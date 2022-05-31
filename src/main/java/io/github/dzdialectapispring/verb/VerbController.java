package io.github.dzdialectapispring.verb;

import java.util.Set;
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

  @GetMapping("/{id}")
  public Verb getVerbById(@PathVariable String id) {
    return verbService.getVerbById(id);
  }


}
