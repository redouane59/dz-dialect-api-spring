package io.github.dzdialectapispring.pronoun;

import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/pronouns")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PronounsController {

  private final PronounService pronounService;

  @GetMapping
  public List<SentenceDTO> getAllPronouns() {
    return pronounService.getAllPronouns();
  }
}
