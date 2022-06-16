package io.github.dzdialectapispring.question;

import static io.github.dzdialectapispring.sentence.SentenceBuilder.RANDOM;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import io.github.dzdialectapispring.other.Config;
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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
@Slf4j
public class QuestionService {

  private final String              path                = "questions";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  public List<AbstractQuestion> getAllQuestionObjects() {
    QuerySnapshot query;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    List<AbstractQuestion>
        abstractQuestions =
        documentSnapshot.stream().map(d -> d.toObject(AbstractQuestion.class)).collect(Collectors.toList());
    if (abstractQuestions.size() == 0) {
      LOGGER.error("no pronouns loaded");
    }
    return abstractQuestions;
  }

  public Optional<AbstractQuestion> getRandomAbstractQuestion(Verb verb) {
    List<AbstractQuestion> questions = getAllQuestionObjects();
    if (verb == null) {
      return questions.stream().skip(RANDOM.nextInt(questions.size())).findFirst();
    } else if (!verb.getPossibleQuestionIds().isEmpty()) {
      List<String> questionIds = verb.getPossibleQuestionIds();
      String       questionId  = questionIds.stream().skip(RANDOM.nextInt(questionIds.size())).findFirst().get();
      return getQuestion(questionId);
    } else {
      LOGGER.debug("no question found");
      return Optional.empty();
    }
  }


  public Optional<AbstractQuestion> getQuestion(String questionId) {
    DocumentReference           documentReference = collectionReference.document(questionId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = null;
    try {
      documentSnapshot = future.get();
    } catch (Exception e) {
      LOGGER.error("enable to get question : " + e.getMessage());
    }
    AbstractQuestion abstractQuestion;
    if (documentSnapshot != null && documentSnapshot.exists()) {
      abstractQuestion = documentSnapshot.toObject(AbstractQuestion.class);
      return Optional.of(abstractQuestion);
    }
    return Optional.empty();
  }

  public void insert(final List<AbstractQuestion> questions) throws ExecutionException, InterruptedException {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    for (AbstractQuestion question : questions) {
      CollectionReference ref = dbFirestore.collection(path);
      if (!ref.document(question.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
        System.out.println("inserting question " + question.getId() + "...");
        dbFirestore.collection(path).document(question.getId()).set(question);
      }
    }
    System.out.println("insert finished");
  }

  public List<SentenceDTO> getAllQuestions() {
    List<SentenceDTO> result = new ArrayList<>();
    for (AbstractQuestion abstractQuestion : getAllQuestionObjects()) {
      for (Word word : abstractQuestion.getValues()) {
        Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getFrTranslationValue()),
                                                 new Translation(Lang.DZ, word.getDzTranslationValue(), word.getDzTranslationValueAr())));
        sentence.setContent(SentenceContent.builder().abstractQuestion(abstractQuestion).build());
        result.add(new SentenceDTO(sentence));
      }
    }
    return result;
  }
}
