package io.github.dzdialectapispring.verb;

import static io.github.dzdialectapispring.other.Config.RANDOM;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
public class VerbService {

  private final String              path                = "verbs";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  @Autowired
  private PronounService pronounService;

  public Set<String> getAllVerbIds() throws ExecutionException, InterruptedException {
    QuerySnapshot               query            = collectionReference.get().get();
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    return documentSnapshot.stream().map(d -> d.toObject(Verb.class)).map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Set<String> getAvailableTenses(String verbId) throws ExecutionException, InterruptedException {
    Verb verb = getVerbById(verbId);
    return verb
        .getValues()
        .stream()
        .map(c -> c.getSubtense().getTense().getId())
        .collect(Collectors.toSet());
  }

  public Set<Verb> getAllVerbs() {
    QuerySnapshot query = null;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      e.printStackTrace();
      return Set.of();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    return documentSnapshot.stream().map(d -> d.toObject(Verb.class)).collect(Collectors.toSet());
  }

  public List<SentenceDTO> getVerbConjugationsById(final String verbId, String tenseId) throws ExecutionException, InterruptedException {
    Verb              verb         = getVerbById(verbId);
    List<Conjugation> conjugations = verb.getValues();
    if (tenseId != null) {
      Optional<Tense> tenseOptional = Arrays.stream(Tense.values()).filter(t -> t.getId().equals(tenseId)).findFirst();
      if (tenseOptional.isEmpty()) {
        throw new IllegalArgumentException("No tense found with id " + tenseId);
      }
      conjugations = conjugations.stream().filter(c -> c.getSubtense().getTense() == tenseOptional.get()).collect(Collectors.toList());
    }
    List<SentenceDTO> result = new ArrayList();
    for (Conjugation conjugation : conjugations) {
      AbstractPronoun
          abstractPronoun =
          pronounService.getAbstractPronoun(conjugation.getGender(), conjugation.isSingular(), conjugation.getPossession());
      PossessiveWord pronoun   = abstractPronoun.getValues().get(0);
      String         frValue   = "";
      String         dzValue   = "";
      String         dzValueAr = "";
      if (conjugation.getSubtense().getTense() != Tense.IMPERATIVE) {
        frValue += pronoun.getFrTranslation() + " ";
        dzValue += pronoun.getDzTranslation() + " ";
        dzValueAr += pronoun.getDzTranslationAr() + " ";
      }
      frValue += conjugation.getFrTranslation();
      dzValue += conjugation.getDzTranslation();
      dzValueAr += conjugation.getDzTranslationAr();
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, frValue), new Translation(Lang.DZ, dzValue, dzValueAr)));
      sentence.setContent(SentenceContent.builder()
                                         .subtense(conjugation.getSubtense())
                                         .abstractPronoun(abstractPronoun)
                                         .abstractVerb(verb)
                                         .build());
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public Optional<Verb> getRandomAbstractVerb(SentenceSchema schema) {
    Set<Verb> verbs = getAllVerbs();
    if (schema.getTenses() != null) {
      verbs = verbs.stream()
                   .filter(v -> v.getValues()
                                 .stream()
                                 .map(Conjugation::getSubtense)
                                 .anyMatch(c -> schema.getTenses().contains(c.getTense()))).collect(
              Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on tenses");
      }
    }
    if (schema.getVerbType() != null) {
      verbs = verbs.stream().filter(v -> v.getVerbType() == schema.getVerbType()).collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on type (" + schema.getVerbType() + " expected)");
      }
    }
/*    if (schema.getFrSequence().contains(WordType.QUESTION)) {
      verbs = verbs.stream().filter(v -> v.getPossibleQuestionIds().contains(question.getId())).collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on question");
      }
    }*/
/*    if (schema.getFrSequence().contains(WordType.SUFFIX)) {
      if (schema.getFrSequence().contains(WordType.NOUN)) {
        verbs = verbs.stream().filter(Verb::isIndirectComplement)
                     .collect(Collectors.toSet());
      } else {
        verbs = verbs.stream().filter(Verb::isDirectComplement).collect(Collectors.toSet());
      }
      if (verbs.size() == 0) {
        System.out.println("no verb found based on suffix");
      }
    }*/
/*    if (schema.getFrSequence().contains(WordType.NOUN)) {
      verbs = verbs.stream().filter(v -> !v.getPossibleComplements().isEmpty())
                   .filter(v -> v.getPossibleComplements().size() > 1 || !v.getPossibleComplements().contains(NounType.ADVERB)) // @todo dirty
                   .collect(Collectors.toSet());
      if (verbs.size() == 0) {
        System.out.println("no verb found based on noun complements");
      }
    }*/
    if (verbs.size() == 0) {
      return Optional.empty();
    }
    return verbs.stream().skip(RANDOM.nextInt(verbs.size())).findFirst();
  }

  public void insert(final Verb verb) throws ExecutionException, InterruptedException {
    Firestore           dbFirestore = FirestoreClient.getFirestore();
    CollectionReference ref         = dbFirestore.collection(path);
    if (!ref.document(verb.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
      System.out.println("inserting verb " + verb.getId() + "...");
      dbFirestore.collection(path).document(verb.getId()).set(verb);
    }
  }

  public Verb getVerbById(final String verbId) throws ExecutionException, InterruptedException {
    DocumentReference           documentReference = collectionReference.document(verbId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = future.get();
    Verb                        verb;
    if (documentSnapshot.exists()) {
      verb = documentSnapshot.toObject(Verb.class);
      return verb;
    }
    return null;
  }


}
