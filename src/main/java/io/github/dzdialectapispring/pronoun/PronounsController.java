package io.github.dzdialectapispring.pronoun;

import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/pronouns")
@AllArgsConstructor
public class PronounsController {

  private final PronounService pronounService;

  @GetMapping
  public List<SentenceDTO> getAllPronouns() throws ExecutionException, InterruptedException {
    return pronounService.getAllPronouns();
  }
}
