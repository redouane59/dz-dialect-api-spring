package io.github.dzdialectapispring.sentence;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sentences")
@AllArgsConstructor
public class SentenceController {

  private final SentenceService sentenceService;

  @GetMapping("/generate")
  public Sentence generateRandomSentence(){
    return sentenceService.generateRandomSentence();
  }

  @GetMapping("/{id}")
  public Sentence getSentenceById(@RequestParam String id){
    return sentenceService.getSentenceById(id);
  }

}
