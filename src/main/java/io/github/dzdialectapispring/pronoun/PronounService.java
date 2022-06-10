package io.github.dzdialectapispring.pronoun;

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
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Data
public class PronounService {

  private final String              path                = "pronouns";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  public AbstractPronoun getAbstractPronoun(final Gender gender, final boolean isSingular, final Possession possession)
  throws ExecutionException, InterruptedException {
    Optional<AbstractPronoun> abstractPronounOptional = getAllPronounsObjects().stream()
                                                                               .filter(p -> (p.getValues().get(0)).getPossession()
                                                                                            == possession)
                                                                               .filter(p -> (p.getValues().get(0)).isSingular()
                                                                                            == isSingular)
                                                                               .filter(p -> (p.getValues().get(0)).getGender() == gender
                                                                                            || (p.getValues().get(0)).getGender()
                                                                                               == Gender.X
                                                                                            || gender == Gender.X)
                                                                               .findFirst();
    if (abstractPronounOptional.isEmpty()) {
      throw new IllegalStateException("no abstract pronoun found");
    }
    return abstractPronounOptional.get();
  }

  public PossessiveWord getPronoun(final Gender gender, final boolean isSingular, final Possession possession)
  throws ExecutionException, InterruptedException {
    Optional<PossessiveWord> result = getAllPronounsObjects().stream()
                                                             .map(o -> o.getValues().get(0))//@todo dirty ?
                                                             .map(o -> o)
                                                             .filter(o -> o.isSingular() == isSingular)
                                                             .filter(o -> o.getPossession() == possession)
                                                             .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                                                             .findAny();
    if (result.isEmpty()) {
      throw new IllegalStateException("no pronoun found");
    }
    return result.get();
  }

  public List<AbstractPronoun> getAllPronounsObjects() {
    QuerySnapshot query = null;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    List<AbstractPronoun>       abstractPronouns = documentSnapshot.stream().map(d -> d.toObject(AbstractPronoun.class)).collect(Collectors.toList());
    Collections.sort(abstractPronouns, new Comparator<AbstractPronoun>() {
      public int compare(AbstractPronoun o1, AbstractPronoun o2) {
        if (o1.getValues().get(0).getIndex() == o2.getValues().get(0).getIndex()) {
          return 0;
        }
        return o1.getValues().get(0).getIndex() < o2.getValues().get(0).getIndex() ? -1 : 1;
      }
    });
    return abstractPronouns;
  }

  public List<SentenceDTO> getAllPronouns() throws ExecutionException, InterruptedException {
    List<SentenceDTO> result = new ArrayList<>();
    for (AbstractPronoun abstractPronoun : getAllPronounsObjects()) {
      for (Word word : abstractPronoun.getValues()) {
        Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getFrTranslation()),
                                                 new Translation(Lang.DZ, word.getDzTranslation(), word.getDzTranslationAr())));
        sentence.setContent(SentenceContent.builder().abstractPronoun(abstractPronoun).build());
        result.add(new SentenceDTO(sentence));
      }
    }
    return result;
  }

  public PossessiveWord getRandomPronoun() throws ExecutionException, InterruptedException {
    List<AbstractPronoun> pronouns        = getAllPronounsObjects();
    AbstractPronoun       abstractPronoun = pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
    return abstractPronoun.getValues().get(0);
  }

  public AbstractPronoun getRandomAbstractPronoun(Possession possession) {
    List<AbstractPronoun> pronouns = getAllPronounsObjects().stream()
                                                            .filter(p -> (p.getValues().get(0)).getPossession() == possession)
                                                            .collect(Collectors.toList());
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }

  public AbstractPronoun getRandomAbstractPronoun() {
    List<AbstractPronoun> pronouns = getAllPronounsObjects();
    return pronouns.stream().skip(RANDOM.nextInt(pronouns.size())).findFirst().get();
  }

  public void insert(final List<AbstractPronoun> pronouns) throws ExecutionException, InterruptedException {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    for (AbstractPronoun pronoun : pronouns) {
      CollectionReference ref = dbFirestore.collection("pronouns");
      if (!ref.document(pronoun.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
        System.out.println("inserting pronoun " + pronoun.getId() + "...");
        dbFirestore.collection("pronouns").document(pronoun.getId()).set(pronoun);
      }
    }
    System.out.println("insert finished");
  }

  public AbstractPronoun getPronounById(final String pronounId) throws ExecutionException, InterruptedException {
    DocumentReference           documentReference = collectionReference.document(pronounId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = future.get();
    AbstractPronoun             abstractPronoun;
    if (documentSnapshot.exists()) {
      abstractPronoun = documentSnapshot.toObject(AbstractPronoun.class);
      return abstractPronoun;
    }
    return null;
  }
}
