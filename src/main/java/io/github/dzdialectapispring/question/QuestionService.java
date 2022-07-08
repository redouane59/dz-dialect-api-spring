package io.github.dzdialectapispring.question;

import static io.github.dzdialectapispring.sentence.SentenceBuilder.RANDOM;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.verb.Verb;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data

public class QuestionService {

  public Optional<AbstractQuestion> getRandomAbstractQuestion(Verb verb) {
    List<AbstractQuestion> questions = DB.QUESTIONS;
    if (verb == null) {
      return questions.stream().skip(RANDOM.nextInt(questions.size())).findFirst();
    } else if (!verb.getPossibleQuestionIds().isEmpty()) {
      List<String> questionIds = verb.getPossibleQuestionIds();
      String       questionId  = questionIds.stream().skip(RANDOM.nextInt(questionIds.size())).findFirst().get();
      return DB.QUESTIONS.stream().findFirst().filter(q -> q.getId().equals(questionId));
    } else {
      System.out.println("no question found");
      return Optional.empty();
    }
  }

  public List<SentenceDTO> getAllQuestions() {
    List<SentenceDTO> result = new ArrayList<>();
    for (AbstractQuestion abstractQuestion : DB.QUESTIONS) {
      for (Word word : abstractQuestion.getValues()) {
        Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getTranslationValue(Lang.FR)),
                                                 new Translation(Lang.DZ, word.getTranslationValue(Lang.DZ), word.getTranslationValueAr(Lang.DZ))));
        sentence.setContent(SentenceContent.builder().abstractQuestion(abstractQuestion).build());
        result.add(new SentenceDTO(sentence));
      }
    }
    return result;
  }

  public AbstractQuestion getQuestionById(String questionId) {
    Optional<AbstractQuestion> questionOpt = DB.QUESTIONS.stream().findFirst().filter(q -> q.getId().equals(questionId));
    return questionOpt.orElseThrow(() -> new IllegalArgumentException("No question found with id " + questionId));
  }
}
