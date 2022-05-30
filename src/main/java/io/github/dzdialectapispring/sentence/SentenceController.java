package io.github.dzdialectapispring.sentence;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sentences/generate")
@AllArgsConstructor
public class SentenceController {

  private final SentenceService sentenceService;

  @GetMapping
  public Sentence generateRandomSentence(){
    return sentenceService.generateRandomSentence();
  }

}
