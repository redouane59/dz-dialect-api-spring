package io.github.dzdialectapispring.sentence;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sentences")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
@Slf4j
public class SentenceController {

  private final SentenceService sentenceService;

  @GetMapping("/generate")
  public List<SentenceDTO> generateRandomSentence(@RequestParam(required = false) Integer count,
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
  public String addSentence(@NonNull @RequestBody ContributionSentence sentence, @RequestHeader("x-authorization-id") String header) {
    if (header.equals("123VivaLalgerie")) {
      return sentenceService.insertSentence(sentence);
    } else {
      LOGGER.debug("invalid header " + header);
      return null;
    }
  }

  @GetMapping("/{id}")
  public ContributionSentenceDTO getSentenceById(@PathVariable String id) {
    return sentenceService.getSentenceById(id);
  }

}
