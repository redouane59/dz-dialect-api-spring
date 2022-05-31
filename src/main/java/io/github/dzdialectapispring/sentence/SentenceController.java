package io.github.dzdialectapispring.sentence;

import io.github.dzdialectapispring.other.enumerations.RootTense;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sentences")
@AllArgsConstructor
public class SentenceController {

  private final SentenceService sentenceService;

  @GetMapping("/generate")
  public List<Sentence> generateRandomSentence(@RequestParam(required = false) Integer count,
                                               @RequestParam(required = false) RootTense tense) {
    return sentenceService.generateRandomSentences(count, tense);
  }

  @GetMapping("/{id}")
  public Sentence getSentenceById(@PathVariable String id) {
    return sentenceService.getSentenceById(id);
  }

}
