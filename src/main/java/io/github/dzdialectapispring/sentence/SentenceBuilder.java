package io.github.dzdialectapispring.sentence;


import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.adjective.AdjectiveService;
import io.github.dzdialectapispring.adverb.AdverbService;
import io.github.dzdialectapispring.noun.Noun;
import io.github.dzdialectapispring.noun.NounService;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.Preposition;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.abstracts.DefinedArticles;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.question.AbstractQuestion;
import io.github.dzdialectapispring.question.QuestionService;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import io.github.dzdialectapispring.verb.VerbType;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import io.github.dzdialectapispring.verb.suffix.Suffix;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
public class SentenceBuilder {

  public static Random           RANDOM = new Random();
  private final PronounService   pronounService;
  private final VerbService      verbService;
  private final QuestionService  questionService;
  private final AdjectiveService adjectiveService;
  private final AdverbService    adverbService;
  private final NounService      nounService;
  List<WordTypeWordTuple> wordListFr;
  List<WordTypeWordTuple> wordListAr;
  PossessiveWord          subject          = null;
  Noun                    nounSubject      = null;
  AbstractWord            abstractSubject  = null;
  AbstractQuestion        abstractQuestion = null;
  private PossessiveWord  suffix;
  private SentenceContent sentenceContent = SentenceContent.builder().build();

  @Autowired
  public SentenceBuilder(PronounService pronounService, VerbService verbService, QuestionService questionService,
                         AdjectiveService adjectiveService, AdverbService adverbService, NounService nounService) {
    this.pronounService   = pronounService;
    this.verbService      = verbService;
    this.questionService  = questionService;
    this.adjectiveService = adjectiveService;
    this.adverbService    = adverbService;
    this.nounService      = nounService;
  }

  public Optional<Sentence> generate(GeneratorParameters generatorParameters, boolean removeIdsFromPropositions) {
    sentenceContent = SentenceContent.builder().build();
    boolean resultOk = fillWordListFromSchema(generatorParameters);
    if (!resultOk) {
      System.err.println("no sentence generated with parameters : " + generatorParameters);
      return Optional.empty();
    }
    Sentence sentence = new Sentence();
    sentence.getTranslations().add(generateArTranslation(Lang.DZ));
    Translation frTranslation = generateFrTranslation();
    Translation dzTranslation = generateArTranslation(Lang.DZ);
    sentence.getTranslations().add(frTranslation);
    // word_propositions part
    sentence.getRandomWords().put(Lang.FR, SentenceBuilderHelper.splitSentenceInWords(frTranslation, false));
    sentence.getRandomWords().put(Lang.DZ, SentenceBuilderHelper.splitSentenceInWords(dzTranslation, false));
    sentence.setContent(sentenceContent);
    // generating a second random sentence
    addRandomWordPropositions(sentence, generatorParameters, removeIdsFromPropositions);
    Collections.shuffle(sentence.getRandomWords().get(Lang.FR));
    Collections.shuffle(sentence.getRandomWords().get(Lang.DZ));
    return Optional.of(sentence);
  }

  // @todo to fix when fixed ids
  private void addRandomWordPropositions(Sentence sentence, GeneratorParameters generatorParameters, boolean removeIdsFromPropositions) {
/*    if (sentence.getContent().getAbstractPronoun() != null
        && !sentence.getContent().getSentenceSchema().getId().equals("P")) { // to avoid having bad choices for P sentences
      PossessiveWord pronoun = sentence.getContent().getAbstractPronoun().getValues().get(0);
      generatorParameters.setAbstractPronoun(pronounService.getRandomAbstractPronoun(pronoun.getPossession()));
    }*/

    String sentenceId = sentence.getContent().getSentenceSchema().getId();

    if (sentenceId.contains("A")) {
      generatorParameters.setAbstractVerb(null);
      generatorParameters.setAdjective(null);
      int i = 0;
      while (i < generatorParameters.getAlternativeCount()) {
        fillWordListFromSchema(generatorParameters);
        Word adj = wordListFr.stream().filter(w -> w.getWordType() == WordType.ADJECTIVE).findFirst().get().getWord();
        if (!sentence.getRandomWords().get(Lang.FR).contains(adj.getTranslationValue(Lang.FR))) {
          sentence.getRandomWords().get(Lang.FR).add(adj.getTranslationValue(Lang.FR));
          sentence.getRandomWords().get(Lang.DZ).add(adj.getTranslationValue(Lang.DZ));
          i++;
        }
      }
      return;
    } else if (sentenceId.equals("PV") || sentenceId.equals("P")) {
      generatorParameters.setAbstractPronoun(null);
      Set<String> frValues = new HashSet<>(sentence.getRandomWords().get(Lang.FR));
      Set<String> dzValues = new HashSet<>(sentence.getRandomWords().get(Lang.DZ));
      int         i        = 0;
      while (i < generatorParameters.getAlternativeCount()) {
        fillWordListFromSchema(generatorParameters);
        String dzWord = SentenceBuilderHelper.splitSentenceInWords(generateArTranslation(Lang.DZ), true).get(0);
        if (!dzValues.contains(dzWord)) { // to avoid having less values than expected
          i++;
        }
        frValues.addAll(SentenceBuilderHelper.splitSentenceInWords(generateFrTranslation(), true));
        dzValues.addAll(SentenceBuilderHelper.splitSentenceInWords(generateArTranslation(Lang.DZ), true));

      }
      sentence.getRandomWords().replace(Lang.FR, new ArrayList<>(frValues));
      sentence.getRandomWords().replace(Lang.DZ, new ArrayList<>(dzValues));
      return;
    }

    if (removeIdsFromPropositions) {
      generatorParameters.setAbstractPronoun(null);
      generatorParameters.setAbstractVerb(null);
      generatorParameters.setAdjective(null);
    }
    for (int i = 0; i < generatorParameters.getAlternativeCount(); i++) {
      fillWordListFromSchema(generatorParameters);
      sentence.getRandomWords().get(Lang.FR).addAll(SentenceBuilderHelper.splitSentenceInWords(generateFrTranslation(), true));
      sentence.getRandomWords().get(Lang.DZ).addAll(SentenceBuilderHelper.splitSentenceInWords(generateArTranslation(Lang.DZ), true));
    }
  }

  public void resetAttributes(GeneratorParameters generatorParameters) {

    nounSubject      = null;
    abstractQuestion = null;
    subject          = null;
    abstractSubject  = null;
    wordListFr       = new ArrayList<>();
    wordListAr       = new ArrayList<>();
    sentenceContent  = SentenceContent.builder().build();
    sentenceContent.setSentenceSchema(generatorParameters.getSentenceSchema());
    if (sentenceContent.getSentenceSchema().isPossibleNegation()) {
      if (generatorParameters.isExcludeNegative() == generatorParameters.isExcludePositive()) {
        sentenceContent.setNegation(RANDOM.nextBoolean());
      } else {
        sentenceContent.setNegation(generatorParameters.isExcludePositive());
      }
    }
  }

  // @todo split FR & DZ
  private boolean fillWordListFromSchema(GeneratorParameters generatorParameters) {
    resetAttributes(generatorParameters);
    for (int index = 0; index < sentenceContent.getSentenceSchema().getFrSequence().size(); index++) {
      WordType wordType = sentenceContent.getSentenceSchema().getFrSequence().get(index);
      boolean  success;
      switch (wordType) {
        case PRONOUN:
          buildPronoun(index, generatorParameters.getAbstractPronoun());
          break;
        case NOUN:
          success = buildNoun(index);
          if (!success) {
            return false;
          }
          break;
        case VERB:
          success = buildVerb(index, generatorParameters.getAbstractVerb(), generatorParameters.getTense());
          if (!success) {
            return false;
          }
          break;
        case ADJECTIVE:
          success = buildAdjective(index, generatorParameters.getAdjective());
          if (!success) {
            return false;
          }
          break;
        case ADVERB:
          success = builAdverb(index);
          break;
        case QUESTION:
          success = buildQuestion(index, generatorParameters.getAbstractVerb());
          if (!success) {
            return false;
          }
          break;
      }
    }
    return true;
  }

  public boolean buildPronoun(int index, AbstractPronoun abstractPronoun) {
    if (abstractPronoun == null) {
      abstractPronoun = pronounService.getRandomAbstractPronoun();
    }
    if (abstractPronoun != null) {
      abstractSubject = abstractPronoun;
    }
    PossessiveWord pronoun = abstractPronoun.getValues().get(0);
    sentenceContent.setAbstractPronoun(abstractPronoun);
    wordListFr.add(new WordTypeWordTuple(WordType.PRONOUN, pronoun, index));
    wordListAr.add(new WordTypeWordTuple(WordType.PRONOUN, pronoun, index));
    if (sentenceContent.getSentenceSchema().getSubjectPosition() == index) {
      subject = pronoun;
    }
    return true;
  }

  private boolean buildNoun(int index) {
    Optional<Noun> nounOpt = nounService.getRandomNoun(sentenceContent.getAbstractVerb());
    if (nounOpt.isEmpty()) {
      return false;
    }
    this.nounSubject = nounOpt.get();
    PossessiveWord noun        = new PossessiveWord();
    GenderedWord   concretNoun = nounSubject.getValues().stream().filter(n -> n.isSingular()).findFirst().get(); // @todo dirty
    noun.setTranslations(concretNoun.getTranslations());
    noun.setSingular(concretNoun.isSingular());
    noun.setGender(concretNoun.getGender());
    noun.setPossession(Possession.OTHER);
    Optional<GenderedWord> frArticle = DefinedArticles.getArticle(noun, Lang.FR);
    Optional<GenderedWord> dzArticle = DefinedArticles.getArticle(noun, Lang.DZ);
    frArticle.ifPresent(genderedWord -> wordListFr.add(new WordTypeWordTuple(WordType.ARTICLE, genderedWord, index)));
    dzArticle.ifPresent(genderedWord -> wordListAr.add(new WordTypeWordTuple(WordType.ARTICLE, genderedWord, index)));

    if (sentenceContent.getAbstractVerb() != null && sentenceContent.getSentenceSchema().getFrSequence().contains(WordType.PREPOSITION)) {
      Preposition preposition;
      File prepositionFile = new File("./src/main/resources/static/prepositions/"
                                      + nounSubject.getDeplacementPrepositionId()
                                      + ".json");
      try { // @todo dirty
        preposition =
            Config.OBJECT_MAPPER.readValue(prepositionFile,
                                           Preposition.class);
      } catch (IOException e) {
        System.err.println(e.getMessage());
        return false;
      }
      if (prepositionFile.exists()) {
        if (sentenceContent.getAbstractVerb().getVerbType() == VerbType.DEPLACEMENT) {
          wordListFr.add(new WordTypeWordTuple(WordType.PREPOSITION,
                                               preposition.getWordByGenderAndSingular(noun.getGender(Lang.FR), Lang.FR, true).get(),
                                               index));
          wordListAr.add(new WordTypeWordTuple(WordType.PREPOSITION,
                                               preposition.getWordByGenderAndSingular(noun.getGender(Lang.DZ), Lang.DZ, true).get(),
                                               index));
        } else if (sentenceContent.getAbstractVerb().getVerbType() == VerbType.STATE) {
          wordListFr.add(new WordTypeWordTuple(WordType.PREPOSITION,
                                               preposition.getWordByGenderAndSingular(noun.getGender(Lang.FR), Lang.FR, true).get(),
                                               index));
          wordListAr.add(new WordTypeWordTuple(WordType.PREPOSITION,
                                               preposition.getWordByGenderAndSingular(noun.getGender(Lang.DZ), Lang.DZ, true).get(),
                                               index));
        }
      }
    }

    sentenceContent.setAbstractNoun(nounSubject);
    wordListFr.add(new WordTypeWordTuple(WordType.NOUN, noun, index));
    wordListAr.add(new WordTypeWordTuple(WordType.NOUN, noun, index));
    if (sentenceContent.getSentenceSchema().getSubjectPosition() == index) {
      subject         = noun;
      abstractSubject = nounSubject;
    }
    return true;
  }

  public boolean buildVerb(int index, Verb abstractVerb, Tense tense) {
    if (abstractVerb == null) {
      Optional<Verb> abstractVerbOpt = verbService.getRandomAbstractVerb(sentenceContent.getSentenceSchema(), abstractQuestion);
      if (abstractVerbOpt.isEmpty()) {
        return false;
      }
      abstractVerb = abstractVerbOpt.get(); // if given verb is null, set a random verb
    }
    sentenceContent.setAbstractVerb(abstractVerb);
    Subtense subtense;
    if (tense == null) {
      if (!sentenceContent.getSentenceSchema().getTenses().isEmpty() && sentenceContent.getSentenceSchema().getTenses().contains(Tense.IMPERATIVE)) {
        subtense = Subtense.IMPERATIVE;
      } else {
        Set<Subtense> availableTenses = abstractVerb.getValues().stream().map(Conjugation::getSubtense)
                                                    .filter(t -> sentenceContent.getSentenceSchema().getTenses().contains(t.getTense()))
                                                    .filter(t -> t != Subtense.IMPERATIVE)
                                                    .collect(Collectors.toSet());
        Optional<Subtense> subtenseOptional = availableTenses.stream().skip(RANDOM.nextInt(availableTenses.size())).findFirst();
        if (subtenseOptional.isEmpty()) {
          System.out.println("tense " + tense + " not found for verb " + abstractVerb.getId());
          return false;
        }
        subtense = subtenseOptional.get();
      }
    } else {
      Optional<Subtense> subtenseOptional = abstractVerb.getValues().stream().map(Conjugation::getSubtense)
                                                        .filter(t -> t.getTense() == tense).findFirst();
      if (subtenseOptional.isEmpty()) {
        System.out.println("tense " + tense + " not found for verb " + abstractVerb.getId());
        return false;
      }
      subtense = subtenseOptional.get();
    }

    sentenceContent.setSubtense(subtense);
    if (subtense.getTense() == Tense.PAST) {
      sentenceContent.setNegation(false);
    }
    if (subtense != Subtense.IMPERATIVE) {
      wordListFr.add(new WordTypeWordTuple(WordType.VERB, abstractVerb.getVerbConjugation(subject, subtense, Lang.FR), index));
      wordListAr.add(new WordTypeWordTuple(WordType.VERB, abstractVerb.getVerbConjugation(subject, subtense, Lang.DZ), index));
    } else {
      PossessiveWord        randomPronoun = pronounService.getRandomImperativePersonalPronoun();
      Optional<Conjugation> frConjugation = abstractVerb.getImperativeVerbConjugation(randomPronoun, Lang.FR, sentenceContent.isNegation());
      if (frConjugation.isEmpty()) {
        System.out.println("no imperative verb conjugation FR found");
        return false;
      }
      wordListFr.add(new WordTypeWordTuple(WordType.VERB, frConjugation.get(), index));
      Optional<Conjugation> arConjugation = abstractVerb.getImperativeVerbConjugation(randomPronoun, Lang.DZ, sentenceContent.isNegation());
      if (arConjugation.isEmpty()) {
        System.out.println("no imperative verb conjugation AR found");
        return false;
      }
      wordListAr.add(new WordTypeWordTuple(WordType.VERB, arConjugation.get(), index));
    }
    if (sentenceContent.getSentenceSchema().getFrSequence().contains(WordType.SUFFIX)) {
      Optional<PossessiveWord> suffixOpt;
      if (sentenceContent.getSubtense().getTense() == Tense.IMPERATIVE) {
        suffixOpt = Suffix.getSuffix(abstractVerb, sentenceContent.getSentenceSchema(), null, abstractVerb.isObjectOnly(), true);
      } else {
        suffixOpt = Suffix.getSuffix(abstractVerb, sentenceContent.getSentenceSchema(), subject.getPossession(), abstractVerb.isObjectOnly(), false);
      }
      if (suffixOpt.isEmpty()) {
        return false;
      }
      suffix = suffixOpt.get();
      wordListFr.add(new WordTypeWordTuple(WordType.SUFFIX, suffix, index));
      wordListAr.add(new WordTypeWordTuple(WordType.SUFFIX, suffix, index));
    }
    return true;
  }

  public boolean buildAdjective(int index, Adjective adjective) {
    if (adjective == null) {
      Optional<Adjective> adjectiveOpt = adjectiveService.getAbstractAdjective(sentenceContent.getSentenceSchema(), abstractSubject, nounSubject);
      if (adjectiveOpt.isEmpty()) {
        return false;
      }
      adjective = adjectiveOpt.get();
    }
    sentenceContent.setAbstractAdjective(adjective);
    wordListFr.add(new WordTypeWordTuple(WordType.ADJECTIVE, Adjective.getAdjective(adjective, subject, Lang.FR).get(), index));
    wordListAr.add(new WordTypeWordTuple(WordType.ADJECTIVE, Adjective.getAdjective(adjective, subject, Lang.DZ).get(), index));

    return true;
  }

  private boolean builAdverb(int index) {
    AbstractWord adverb = adverbService.getRandomAdverb(this.getSentenceContent().getAbstractVerb());
    sentenceContent.setAbstractAdverb(adverb);
    wordListFr.add(new WordTypeWordTuple(WordType.ADVERB, adverb.getValues().get(0), index));
    wordListAr.add(new WordTypeWordTuple(WordType.ADVERB, adverb.getValues().get(0), index));
    return true;
  }

  private boolean buildQuestion(int index, Verb abstractVerb) {
    Optional<AbstractQuestion> abstractQuestionOpt = questionService.getRandomAbstractQuestion(abstractVerb);
    if (abstractQuestionOpt.isEmpty()) {
      return false;
    }
    this.abstractQuestion = abstractQuestionOpt.get();
    sentenceContent.setAbstractQuestion(abstractQuestion);
    Word question = abstractQuestion.getValues().get(0);
    wordListFr.add(new WordTypeWordTuple(WordType.QUESTION, question, index));
    wordListAr.add(new WordTypeWordTuple(WordType.QUESTION, question, index));
    return true;
  }


  private Translation generateFrTranslation() {
    StringBuilder sentenceValue = new StringBuilder();
    for (int i = 0; i < sentenceContent.getSentenceSchema().getFrSequence().size(); i++) {
      WordType wordType = sentenceContent.getSentenceSchema().getFrSequence().get(i);
      if (wordType == WordType.SUFFIX && sentenceContent.getSubtense() == Subtense.IMPERATIVE) { // add imperative condition
        sentenceValue.deleteCharAt(sentenceValue.length() - 1);
        sentenceValue.append("-");
      }
      if (wordType == WordType.VERB
          && sentenceContent.isNegation()
          && sentenceContent.getSubtense().getTense() != Tense.PAST) {
        sentenceValue.append("ne "); // @todo use Traduction class and remove it
      }
      Word w = getFirstWordFromWordTypeFr(wordType, i);
      // to manage adjectives with auxiliar avoir in french
      if (wordType == WordType.VERB
          && sentenceContent.getSentenceSchema().getFrSequence().contains(WordType.ADJECTIVE)
          && sentenceContent.getAbstractAdjective().isFrAuxiliarAvoir()) {
        w = DB.AUX_AVOIR.getConjugationByGenderSingularPossessionAndTense(subject.getGender(Lang.FR),
                                                                          subject.isSingular(),
                                                                          subject.getPossession(),
                                                                          sentenceContent.getSubtense().getTense()).get();
      }
      if (w != null) {
        sentenceValue.append(w.getTranslationValue(Lang.FR));
      }
      if (wordType == WordType.VERB
          && sentenceContent.isNegation()
          && sentenceContent.getSubtense().getTense() != Tense.PAST) {
        sentenceValue.append(" pas");
      }

      sentenceValue.append(" ");
    }
    sentenceValue.append(completeSentence(false));
    return new Translation(Lang.FR, sentenceValue.toString());
  }

  private Translation generateArTranslation(Lang lang) {
    StringBuilder sentenceValue   = new StringBuilder();
    StringBuilder sentenceValueAr = new StringBuilder();
    for (int i = 0; i < sentenceContent.getSentenceSchema().getArSequence().size(); i++) {
      WordType wordType = sentenceContent.getSentenceSchema().getArSequence().get(i);
      if (wordType == WordType.SUFFIX) {
        String suffixDzValue;
        sentenceValue.deleteCharAt(sentenceValue.length() - 1);
        sentenceValueAr.deleteCharAt(sentenceValueAr.length() - 1);
/*        if (getSentenceContent().getAbstractAdverb().isDzOppositeComplement()) {
          suffixDzValue = AbstractWord.getOppositeSuffix(suffix).getTranslationValue(lang);
          sentenceValueAr.append(AbstractWord.getOppositeSuffix(suffix).getTranslationByLang(Lang.DZ).get().getArValue());
        } else {*/
        suffixDzValue = getFirstWordFromWordTypeFr(wordType, i).getTranslationByLang(lang).get().getValue();
        sentenceValueAr.append(getFirstWordFromWordTypeFr(wordType, i).getTranslationByLang(lang).get().getArValue());
        //     }
        sentenceValue.append(suffixDzValue);
        // manage transformation here iou -> ih, ouou->ou, etc.
/*        for (Entry<String, String> m : DB.RULE_MAP.entrySet()) {
          sentenceValue = new StringBuilder(sentenceValue.toString().replace(m.getKey(), m.getValue()));
        }*/
      } else {
        if (sentenceContent.isNegation() && sentenceContent.getSubtense().getTense() != Tense.PAST) {
          if (wordType == WordType.VERB) {
            sentenceValue.append("ma ");
            sentenceValueAr.append("ما ");
          } else if (wordType == WordType.ADJECTIVE
                     && sentenceContent.getAbstractAdjective() != null && sentenceContent.getAbstractAdjective().isDefinitive()
          ) {
            sentenceValue.append("machi ");
            sentenceValueAr.append("ماشي ");
          }
        }
        Word   w       = getFirstWordFromWordTypeAr(wordType, i);
        String arValue = "";
        if (w != null) {
          sentenceValue.append(w.getTranslationByLang(lang).get().getValue());
          arValue = w.getTranslationByLang(lang).get().getArValue();
        }
        sentenceValueAr.append(Objects.requireNonNullElse(arValue, " ٠٠٠ "));
        if (wordType == WordType.VERB && sentenceContent.isNegation() && sentenceContent.getSubtense().getTense() != Tense.PAST) {
          if (sentenceContent.getAbstractAdjective() == null || sentenceContent.getAbstractAdjective().isTemporal()) {
            sentenceValue.append("ch ");
            sentenceValueAr.append("ش");
          }
        }
      }
      sentenceValue.append(" ");
      sentenceValueAr.append(" ");
    }
    sentenceValue.append(completeSentence(false));
    sentenceValueAr.append(completeSentence(true));
    return new Translation(lang, sentenceValue.toString(), sentenceValueAr.toString());
  }

  private String completeSentence(boolean arabValue) {
    String result = "";
    if (sentenceContent.getSentenceSchema().getFrSequence().contains(WordType.QUESTION)) {
      if (arabValue) {
        result += "؟";
      } else {
        result += "?";
      }
    }
    if (sentenceContent.getSubtense() == Subtense.IMPERATIVE) {
      if (arabValue) {
        result += "!\u200F";
      } else {
        result += "!";
      }
    }
    return result;
  }

  // @todo dirty
  public Word getFirstWordFromWordTypeFr(WordType wordType, int position) {
    Optional<WordTypeWordTuple> opt = wordListFr.stream().filter(w -> w.getWordType() == wordType).findFirst();
    if (opt.isEmpty()) {
      return null;
    } else {
      return opt.get().getWord();
    }
  }

  public Word getFirstWordFromWordTypeAr(WordType wordType, int position) {
    Optional<WordTypeWordTuple> opt = wordListAr.stream().filter(w -> w.getWordType() == wordType).findFirst();
    if (opt.isEmpty()) {
      return null;
    } else {
      return opt.get().getWord();
    }
  }


  @AllArgsConstructor
  @Getter
  @Setter
  public static class WordTypeWordTuple {

    private WordType wordType;
    private Word     word;
    private int      position;
  }

}
