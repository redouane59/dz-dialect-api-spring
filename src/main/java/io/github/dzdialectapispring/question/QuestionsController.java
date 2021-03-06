package io.github.dzdialectapispring.question;

import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/questions")
@AllArgsConstructor
@CrossOrigin(origins = "https://dz-dialect-app.herokuapp.com/", allowedHeaders = "*")
public class QuestionsController {

  private final QuestionService questionService;

  @GetMapping
  public List<SentenceDTO> getAllQuestions() {
    return questionService.getAllQuestions();
  }
}
