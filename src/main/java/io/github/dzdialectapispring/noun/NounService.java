package io.github.dzdialectapispring.noun;

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
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.sentence.WordDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NounService {

  private final String              path                = "nouns";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);

  public Set<String> getAllNounsIds() {
    return getAllNounObjects().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  private Set<Noun> getAllNounObjects() {
    QuerySnapshot query;
    try {
      query = collectionReference.get().get();
    } catch (Exception e) {
      LOGGER.error("enable to get all nouns " + e.getMessage());
      return Set.of();
    }
    List<QueryDocumentSnapshot> documentSnapshot = query.getDocuments();
    return documentSnapshot.stream().map(d -> d.toObject(Noun.class)).collect(Collectors.toSet());
  }

  public Noun getNounById(final String nounId) {
    DocumentReference           documentReference = collectionReference.document(nounId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = null;
    try {
      documentSnapshot = future.get();
    } catch (Exception e) {
      LOGGER.error("enable to find noun " + nounId + " " + e.getMessage());
    }
    Noun noun;
    if (documentSnapshot.exists()) {
      noun = documentSnapshot.toObject(Noun.class);
      return noun;
    }
    return null;
  }

  public List<WordDTO> getNounValuesById(final String nounId) {
    Noun          noun   = getNounById(nounId);
    List<WordDTO> result = new ArrayList<>();
    for (GenderedWord word : noun.getValues()) {
      result.add(new WordDTO(word));
    }
    return result;
  }

  public void insert(final Noun noun) throws ExecutionException, InterruptedException {
    Firestore           dbFirestore = FirestoreClient.getFirestore();
    CollectionReference ref         = dbFirestore.collection(path);
    if (!ref.document(noun.getId()).get().get().exists() || Config.FORCE_OVERRIDE) {
      System.out.println("inserting adjective " + noun.getId() + "...");
      dbFirestore.collection(path).document(noun.getId()).set(noun);
    }
  }
}
