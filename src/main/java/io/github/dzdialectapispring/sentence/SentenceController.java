package io.github.dzdialectapispring.sentence;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sentences")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect.bdsapp.fr/", allowedHeaders = "*")
public class SentenceController {

  private final SentenceService sentenceService;
  private final String          headerId = "123VivaLalgerie";

  @GetMapping("/generate")
  public List<SentenceDTO> generateRandomSentence(@RequestParam(required = false, defaultValue = "1") Integer count,
                                                  @RequestParam(required = false, name = "alternative_count", defaultValue = "1")
                                                      Integer alternativeCount,
                                                  @RequestParam(required = false, name = "pronoun") String pronounId,
                                                  @RequestParam(required = false, name = "verb") String verbId,
                                                  @RequestParam(required = false, name = "tense") String tenseId,
                                                  @RequestParam(required = false, name = "noun") String nounId,
                                                  @RequestParam(required = false, name = "adjective") String adjectiveId,
                                                  @RequestParam(required = false, name = "question") String questionId,
                                                  @RequestParam(required = false, name = "adverb") String adverb,
                                                  @RequestParam(required = false, name = "exclude_positive") boolean excludePositive,
                                                  @RequestParam(required = false, name = "exclude_negative") boolean excludeNegative,
                                                  @RequestParam(required = false, name = "sentence_schema") String sentenceSchemaId
  ) throws ExecutionException, InterruptedException {
    return sentenceService.generateRandomSentences(count,
                                                   alternativeCount,
                                                   pronounId,
                                                   verbId,
                                                   tenseId,
                                                   nounId,
                                                   adjectiveId,
                                                   questionId,
                                                   adverb,
                                                   excludePositive,
                                                   excludeNegative, sentenceSchemaId);
  }

  @GetMapping("/schemas")
  public List<String> getSentenceSchemas() {
    return sentenceService.getSentenceSchemas().stream().map(SentenceSchema::getId).collect(Collectors.toList());
  }

  @PostMapping
  @ResponseBody
  public ContributionSentenceDTO addSentence(@NonNull @RequestBody ContributionSentence sentence,
                                             @RequestHeader("x-authorization-id") String header) {
    if (header.equals(headerId)) {
      return sentenceService.insertSentence(sentence);
    } else {
      System.out.println("invalid header " + header);
      return null;
    }
  }

  @GetMapping("/{id}")
  public ContributionSentenceDTO getSentenceById(@PathVariable String id) {
    return sentenceService.getContributionSentenceById(id);
  }

  @PutMapping("/{id}")
  public ContributionSentenceDTO incrementThumb(@PathVariable String id, boolean up, @RequestHeader("x-authorization-id") String header) {
    if (header.equals(headerId)) {
      return sentenceService.incrementThumb(id, up);
    } else {
      System.out.println("invalid header " + header);
      return null;
    }
  }

}
