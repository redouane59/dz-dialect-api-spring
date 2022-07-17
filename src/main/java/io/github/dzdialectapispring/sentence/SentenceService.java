package io.github.dzdialectapispring.sentence;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import io.github.dzdialectapispring.adjective.AdjectiveService;
import io.github.dzdialectapispring.adverb.adjective.AdverbService;
import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.noun.NounService;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.question.QuestionService;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SentenceService {

  private final String              path                = "sentences";
  private final CollectionReference collectionReference = FirestoreClient.getFirestore().collection(path);
  private       SentenceBuilder     sentenceBuilder;
  @Autowired
  private       VerbService         verbService;
  @Autowired
  private       PronounService      pronounService;
  @Autowired
  private       QuestionService     questionService;
  @Autowired
  private       AdjectiveService    adjectiveService;
  @Autowired
  private       AdverbService       adverbService;
  @Autowired
  private       NounService         nounService;

  public List<SentenceDTO> generateRandomSentences(Integer count,
                                                   Integer alternativeCount,
                                                   String pronounId,
                                                   String verbId,
                                                   String tenseId,
                                                   String nounId,
                                                   String adjectiveId,
                                                   String questionId,
                                                   String adverbId,
                                                   boolean excludePositive,
                                                   boolean excludeNegative,
                                                   String sentenceSchemaId) throws ExecutionException, InterruptedException {
    if (count <= 0) {
      throw new IllegalArgumentException("count argument should be positive");
    } else if (count > 30) {
      throw new IllegalArgumentException("count argument should be less than 30");
    }
    if (alternativeCount <= 0) {
      throw new IllegalArgumentException("alternativeCount argument should be positive");
    } else if (alternativeCount > 4) {
      throw new IllegalArgumentException("alternativeCount argument should be less than 5");
    }

    GeneratorParameters
        generatorParameters =
        buildParameters(pronounId,
                        verbId,
                        tenseId,
                        nounId,
                        adjectiveId,
                        questionId,
                        adverbId,
                        excludePositive,
                        excludeNegative,
                        sentenceSchemaId,
                        alternativeCount);
    List<SentenceDTO> result = new ArrayList<>();
    int               i      = 0;
    while (result.size() < count && i < count * 5) { // in case no sentence is generated
      Optional<Sentence> sentence = generateRandomSentence(generatorParameters);
      sentence.ifPresent(value -> result.add(new SentenceDTO(value)));
      i++;
    }
    return result;
  }

  public GeneratorParameters buildParameters(String pronounId, String verbId, String tenseId, String nounId,
                                             String adjectiveId,
                                             String questionId,
                                             String adverbId,
                                             boolean excludePositive,
                                             boolean excludeNegative,
                                             String sentenceSchemaId,
                                             Integer alternativeCount) throws ExecutionException, InterruptedException {
    Verb verb = null;
    if (verbId != null) {
      Optional<Verb> verbOpt = verbService.getVerbById(verbId);
      if (verbOpt.isPresent()) {
        verb = verbOpt.get();
      } else {
        throw new IllegalArgumentException("no verb found with id " + verbId);
      }
    }
    AbstractPronoun pronoun = null;
    if (pronounId != null) {
      pronoun = pronounService.getPronounById(pronounId).get();
    }
    Tense tense = null;
    if (tenseId != null) {
      Optional<Tense> tenseOptional = Tense.findById(tenseId);
      if (tenseOptional.isEmpty()) {
        throw new IllegalArgumentException("no tense found with id " + tenseId);
      }
      tense = tenseOptional.get();
    }
    SentenceSchema schema = null;
    if (sentenceSchemaId != null) {
      Optional<SentenceSchema> sentenceSchemaOptional = getSentenceSchemaById(sentenceSchemaId);
      if (sentenceSchemaOptional.isEmpty()) {
        throw new IllegalArgumentException("no sentence schema found with id " + sentenceSchemaId);
      }
      schema = sentenceSchemaOptional.get();
    }
    return GeneratorParameters.builder()
                              .abstractPronoun(pronoun)
                              .abstractVerb(verb)
                              .tense(tense)
                              .excludePositive(excludePositive)
                              .excludeNegative(excludeNegative)
                              .sentenceSchema(schema)
                              .alternativeCount(alternativeCount)
                              .build();
  }

  public Optional<Sentence> generateRandomSentence(GeneratorParameters generatorParameters) {
    SentenceSchema sentenceSchema;
    if (generatorParameters.getSentenceSchema() == null) {
      Optional<SentenceSchema> sentenceSchemaOpt = getRandomSentenceSchema(generatorParameters);
      if (sentenceSchemaOpt.isEmpty()) {
        return Optional.empty();
      }
      sentenceSchema = sentenceSchemaOpt.get();
    } else {
      sentenceSchema = generatorParameters.getSentenceSchema();
    }
    generatorParameters.setSentenceSchema(sentenceSchema);
    sentenceBuilder = new SentenceBuilder(pronounService, verbService, questionService, adjectiveService, adverbService, nounService);
    return sentenceBuilder.generate(generatorParameters);
  }

  public Optional<SentenceSchema> getSentenceSchemaById(String sentenceSchemaId) {
    return getSentenceSchemas().stream().filter(s -> s.getId().equals(sentenceSchemaId)).findFirst();
  }

  public Set<SentenceSchema> getSentenceSchemas() {
    Set<SentenceSchema> sentenceSchemas = new HashSet<>();
    Set<String>         files           = new HashSet<>(ResourceList.getResources(Pattern.compile(".*sentence_schemas.*json")));
    for (String fileName : files) {
      try {
        sentenceSchemas.add(Config.OBJECT_MAPPER.readValue(new File(fileName), SentenceSchema.class));
      } catch (IOException e) {
        System.err.println("could not load file " + fileName + " -> " + e.getMessage());
      }
    }
    return sentenceSchemas;
  }

  public Optional<SentenceSchema> getRandomSentenceSchema(GeneratorParameters generatorParameters) {

    List<SentenceSchema> matchingSentenceSchema = getSentenceSchemas().stream()
                                                                      .filter(SentenceSchema::isEnabled)
                                                                      .collect(Collectors.toList());
    if (generatorParameters.getAbstractVerb() != null) {
      matchingSentenceSchema = matchingSentenceSchema.stream()
                                                     .filter(s -> s.getFrSequence().contains(WordType.VERB))
                                                     .filter(s -> s.getVerbType() == generatorParameters.getAbstractVerb().getVerbType()
                                                                  || s.getVerbType() == null)
                                                     .collect(Collectors.toList());
    }
    if (generatorParameters.getTense() != null) {
      matchingSentenceSchema = matchingSentenceSchema.stream()
                                                     .filter(s -> s.getFrSequence().contains(WordType.VERB))
                                                     .filter(s -> s.getTenses().contains(generatorParameters.getTense()))
                                                     .collect(Collectors.toList());
    }
    if (matchingSentenceSchema.isEmpty()) {
      throw new IllegalArgumentException("no matching sentence schema found from arguments");
    }
    return Optional.of(matchingSentenceSchema.get(new Random().nextInt(matchingSentenceSchema.size())));
  }

  public Optional<ContributionSentence> getContributionSentenceObjectById(final String sentenceId) {
    DocumentReference           documentReference = collectionReference.document(sentenceId);
    ApiFuture<DocumentSnapshot> future            = documentReference.get();
    DocumentSnapshot            documentSnapshot  = null;
    try {
      documentSnapshot = future.get();
    } catch (Exception e) {
      System.err.println("enable to find sentence " + sentenceId + " " + e.getMessage());
    }
    ContributionSentence sentence;
    if (documentSnapshot.exists()) {
      sentence = documentSnapshot.toObject(ContributionSentence.class);
      return Optional.of(sentence);
    }
    return Optional.empty();
  }

  public ContributionSentenceDTO getContributionSentenceById(final String sentenceId) {
    Optional<ContributionSentence> contributionSentenceOpt = getContributionSentenceObjectById(sentenceId);
    return contributionSentenceOpt.map(ContributionSentenceDTO::new).orElse(null);
  }

  public ContributionSentenceDTO insertSentence(ContributionSentence sentence) {
    DocumentReference addedDocRef = collectionReference.document();
    sentence.setId(addedDocRef.getId());
    ApiFuture<WriteResult> future = addedDocRef.set(sentence);
    try {
      String response = "sentence added at : " + future.get().getUpdateTime() + " with id " + sentence.getId();
      System.out.println(response);
      return new ContributionSentenceDTO(sentence);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
      throw new IllegalArgumentException("Error " + e.getMessage());
    }
  }

  @SneakyThrows
  public ContributionSentenceDTO incrementThumb(final String sentenceId, final boolean up) {
    Optional<ContributionSentence> sentenceOpt = getContributionSentenceObjectById(sentenceId);
    if (sentenceOpt.isEmpty()) {
      throw new IllegalArgumentException("sentence with id " + sentenceId + " not found");
    }
    ContributionSentence newSentence = sentenceOpt.get();
    newSentence.incrementThumb(up);
    ApiFuture<WriteResult> collectionsApiFuture = collectionReference.document(sentenceId).set(newSentence);
    System.out.println("thumb update at " + collectionsApiFuture.get().getUpdateTime());
    return new ContributionSentenceDTO(newSentence);
  }
}
