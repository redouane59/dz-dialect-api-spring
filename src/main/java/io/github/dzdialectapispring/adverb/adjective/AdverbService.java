package io.github.dzdialectapispring.adverb.adjective;

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
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdverbService {

  private final String              path                = "adverbs";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  public void insert(final Adverb adverb) throws ExecutionException, InterruptedException {
    Firestore           dbFirestore = FirestoreClient.getFirestore();
    CollectionReference ref         = dbFirestore.collection(path);
    if (!ref.document(adverb.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
      System.out.println("inserting adverb " + adverb.getId() + "...");
      dbFirestore.collection(path).document(adverb.getId()).set(adverb);
    }
  }

  public Set<Adverb> getAllAdverbObjects() {
    QuerySnapshot query;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      LOGGER.error("enable to get all adverbs " + e.getMessage());
      return Set.of();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    return documentSnapshot.stream().map(d -> d.toObject(Adverb.class)).collect(Collectors.toSet());
  }

  public Set<String> getAllAdverbsIds() {
    return getAllAdverbObjects().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Adverb getAdverbById(final String adverbId) {
    DocumentReference           documentReference = collectionReference.document(adverbId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = null;
    try {
      documentSnapshot = future.get();
    } catch (Exception e) {
      LOGGER.error("enable to find adverb " + adverbId + " " + e.getMessage());
    }
    Adverb adverb;
    if (documentSnapshot.exists()) {
      adverb = documentSnapshot.toObject(Adverb.class);
      return adverb;
    }
    return null;
  }

  public List<SentenceDTO> getAdjectiveValuesById(final String adjectiveId) {
    Adverb            adverb = getAdverbById(adjectiveId);
    List<SentenceDTO> result = new ArrayList<>();
    for (Word word : adverb.getValues()) {
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getTranslationValue(Lang.FR)),
                                               new Translation(Lang.DZ, word.getTranslationValue(Lang.DZ), word.getTranslationValueAr(Lang.DZ))));
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public AbstractWord getAdverb() {
    Set<Adverb> adverbs = getAllAdverbObjects();
    return adverbs.stream().skip(RANDOM.nextInt(adverbs.size())).findFirst().get();
  }
}
