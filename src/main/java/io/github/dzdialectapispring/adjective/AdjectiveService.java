package io.github.dzdialectapispring.adjective;

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
import io.github.dzdialectapispring.other.NounType;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.sentence.Sentence;
import io.github.dzdialectapispring.sentence.SentenceDTO;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdjectiveService {

  private final String              path                = "adjectives";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  public void insert(final Adjective adjective) throws ExecutionException, InterruptedException {
    Firestore           dbFirestore = FirestoreClient.getFirestore();
    CollectionReference ref         = dbFirestore.collection(path);
    if (!ref.document(adjective.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
      System.out.println("inserting adjective " + adjective.getId() + "...");
      dbFirestore.collection(path).document(adjective.getId()).set(adjective);
    }
  }

  public Set<Adjective> getAllAdjectivesObjects() {
    QuerySnapshot query;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      LOGGER.error("enable to get all adjectives " + e.getMessage());
      return Set.of();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    return documentSnapshot.stream().map(d -> d.toObject(Adjective.class)).collect(Collectors.toSet());
  }

  public Set<String> getAllAdjectivesIds() {
    return getAllAdjectivesObjects().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Adjective getAdjectiveById(final String adjectiveId) {
    DocumentReference           documentReference = collectionReference.document(adjectiveId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = null;
    try {
      documentSnapshot = future.get();
    } catch (Exception e) {
      LOGGER.error("enable to find adjective " + adjectiveId + " " + e.getMessage());
    }
    Adjective adjective;
    if (documentSnapshot.exists()) {
      adjective = documentSnapshot.toObject(Adjective.class);
      return adjective;
    }
    return null;
  }

  public List<SentenceDTO> getAdjectiveValuesById(final String adjectiveId) {
    Adjective         adjective = getAdjectiveById(adjectiveId);
    List<SentenceDTO> result    = new ArrayList<>();
    for (GenderedWord word : adjective.getValues()) {
      Sentence sentence = new Sentence(List.of(new Translation(Lang.FR, word.getFrTranslationValue()),
                                               new Translation(Lang.DZ, word.getDzTranslationValue(), word.getDzTranslationValueAr())));
      result.add(new SentenceDTO(sentence));
    }
    return result;
  }

  public Optional<Adjective> getAbstractAdjective(SentenceSchema schema, AbstractWord subject/*, Noun nounSubject*/) {
    Set<NounType> nounTypes = new HashSet<>();
    //  if (subject.getWordType() == WordType.PRONOUN) {
    nounTypes.add(NounType.PERSON);
/*    } else if (nounSubject != null) {
      nounTypes.addAll(nounSubject.getNounTypes());
    }*/

    Set<Adjective> adjectives = getAllAdjectivesObjects();

/*    if (!nounTypes.isEmpty()) {
      adjectives = adjectives.stream().filter(a -> a.getPossibleNouns().stream()
                                                    .anyMatch(nounTypes::contains)).collect(Collectors.toSet());
      if (adjectives.isEmpty()) {
        System.err.println("adjectives empty after noun types");
        return Optional.empty();
      }
    }*/

    if (schema.isDefinitiveAdjective()) {
      adjectives = adjectives.stream().filter(Adjective::isDefinitive).collect(Collectors.toSet());
    } else {
      adjectives = adjectives.stream().filter(Adjective::isTemporal).collect(Collectors.toSet());
    }
    if (adjectives.isEmpty()) {
      LOGGER.debug("adjectives empty after is definitive");
      return Optional.empty();
    }

    Optional<Adjective> adjectiveOpt = adjectives.stream().skip(RANDOM.nextInt(adjectives.size())).findFirst();
    if (adjectiveOpt.isEmpty()) {
      LOGGER.debug("adjective empty");
      return Optional.empty();
    }
    return adjectiveOpt;
  }

}
