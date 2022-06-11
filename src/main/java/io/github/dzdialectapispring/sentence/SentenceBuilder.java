package io.github.dzdialectapispring.sentence;


import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.pronoun.PronounService;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.VerbService;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import java.util.ArrayList;
import java.util.Collections;
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

  public static Random         RANDOM = new Random();
  private final SentenceSchema schema;
  private final PronounService pronounService;
  private final VerbService    verbService;
  List<WordTypeWordTuple> wordListFr;
  List<WordTypeWordTuple> wordListAr;
  PossessiveWord          subject         = null;
  AbstractWord            abstractSubject = null;
  private SentenceContent sentenceContent;

  @Autowired
  public SentenceBuilder(SentenceSchema sentenceSchema, PronounService pronounService, VerbService verbService) {
    this.schema         = sentenceSchema;
    this.pronounService = pronounService;
    this.verbService    = verbService;
  }

  public Optional<Sentence> generate(GeneratorParameters generatorParameters) {
    sentenceContent = SentenceContent.builder().build();
    boolean resultOk = fillWordListFromSchema(generatorParameters);
    if (!resultOk) {
      System.err.println("no sentence generated");
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
    addRandomWordPropositions(sentence, generatorParameters);
    Collections.shuffle(sentence.getRandomWords().get(Lang.FR));
    Collections.shuffle(sentence.getRandomWords().get(Lang.DZ));
    return Optional.of(sentence);
  }

  private void addRandomWordPropositions(Sentence sentence, GeneratorParameters generatorParameters) {
    if (sentence.getContent().getAbstractPronoun() != null) {
      PossessiveWord pronoun = sentence.getContent().getAbstractPronoun().getValues().get(0);
      generatorParameters.setAbstractPronoun(pronounService.getRandomAbstractPronoun(pronoun.getPossession()));
    }
    fillWordListFromSchema(generatorParameters);
    sentence.getRandomWords().get(Lang.FR).addAll(SentenceBuilderHelper.splitSentenceInWords(generateFrTranslation(), true));
    sentence.getRandomWords().get(Lang.DZ).addAll(SentenceBuilderHelper.splitSentenceInWords(generateArTranslation(Lang.DZ), true));
  }

  private void resetAttributes(GeneratorParameters generatorParameters) {
/*
    nounSubject      = null;
    abstractQuestion = null;*/
    subject         = null;
    abstractSubject = null;
    wordListFr      = new ArrayList<>();
    wordListAr      = new ArrayList<>();
    sentenceContent = SentenceContent.builder().build();
    sentenceContent.setSentenceSchema(schema);
    if (schema.isPossibleNegation()) {
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
    for (int index = 0; index < schema.getFrSequence().size(); index++) {
      WordType wordType = schema.getFrSequence().get(index);
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
          success = buildAdjective(index);
          if (!success) {
            return false;
          }
          break;
        case ADVERB:
          builAdverb(index);
          break;
        case QUESTION:
          buildQuestion(index);
          break;
      }
    }
    return true;
  }

  private boolean buildPronoun(int index, AbstractPronoun abstractPronoun) {
    if (abstractPronoun == null) {
      abstractPronoun = pronounService.getRandomAbstractPronoun();
    }
    PossessiveWord pronoun = abstractPronoun.getValues().get(0);
    sentenceContent.setAbstractPronoun(abstractPronoun);
    wordListFr.add(new WordTypeWordTuple(WordType.PRONOUN, pronoun, index));
    wordListAr.add(new WordTypeWordTuple(WordType.PRONOUN, pronoun, index));
    if (schema.getSubjectPosition() == index) {
      subject = pronoun;
    }
    return true;
  }

  private boolean buildNoun(int index) {
  /*  Optional<Noun> abstractNoun = helper.getAbstractNoun(abstractVerb);
    if (abstractNoun.isEmpty()) {
      return false;
    }
    this.nounSubject = abstractNoun.get();
    PossessiveWord         noun      = new PossessiveWord(abstractNoun.get().getWordBySingular(true));
    Optional<GenderedWord> frArticle = helper.getArticle(noun, Lang.FR);
    Optional<GenderedWord> dzArticle = helper.getArticle(noun, Lang.DZ);
    if (abstractVerb != null && schema.getFrSequence().contains(WordType.PREPOSITION)) {
      if (abstractVerb.getVerbType() == VerbType.DEPLACEMENT) {
        wordListFr.add(new WordTypeWordTuple(WordType.PREPOSITION, abstractNoun.get().getDeplacementPreposition(), index));
        wordListAr.add(new WordTypeWordTuple(WordType.PREPOSITION, abstractNoun.get().getDeplacementPreposition(), index));
      } else if (abstractVerb.getVerbType() == VerbType.STATE) {
        wordListFr.add(new WordTypeWordTuple(WordType.PREPOSITION, abstractNoun.get().getStatePreposition(), index));
        wordListAr.add(new WordTypeWordTuple(WordType.PREPOSITION, abstractNoun.get().getStatePreposition(), index));
      }
    } // @todo dirty
    if (getFirstWordFromWordTypeFr(WordType.PREPOSITION, index) == null) {
      frArticle.ifPresent(genderedWord -> wordListFr.add(new WordTypeWordTuple(WordType.ARTICLE, genderedWord, index)));
      dzArticle.ifPresent(genderedWord -> wordListAr.add(new WordTypeWordTuple(WordType.ARTICLE, genderedWord, index)));
    }

    sentenceContent.setAbstractNoun(nounSubject);
    wordListFr.add(new WordTypeWordTuple(WordType.NOUN, noun, index));
    wordListAr.add(new WordTypeWordTuple(WordType.NOUN, noun, index));
    if (schema.getSubjectPosition() == index) {
      subject         = noun;
      abstractSubject = nounSubject;
    }*/
    return true;
  }

  private boolean buildVerb(int index, Verb abstractVerb, Tense tense) {
    if (abstractVerb == null) {
      Optional<Verb> abstractVerbOpt = verbService.getRandomAbstractVerb(schema);
      if (abstractVerbOpt.isEmpty()) {
        return false;
      }
      abstractVerb = abstractVerbOpt.get();
    }
    sentenceContent.setAbstractVerb(abstractVerb);
    Subtense subtense;
    if (tense == null) {
      if (!schema.getTenses().isEmpty() && schema.getTenses().contains(Tense.IMPERATIVE)) {
        subtense = Subtense.IMPERATIVE;
      } else {
        Set<Subtense> availableTenses = abstractVerb.getValues().stream().map(o -> o).map(Conjugation::getSubtense)
                                                    .filter(t -> schema.getTenses().contains(t.getTense()))
                                                    .filter(t -> t != Subtense.IMPERATIVE)
                                                    .collect(Collectors.toSet());
        subtense = availableTenses.stream().skip(RANDOM.nextInt(availableTenses.size())).findFirst().get();
      }
    } else {
      subtense = abstractVerb.getValues().stream().map(o -> o).map(Conjugation::getSubtense)
                             .filter(t -> t.getTense() == tense).findFirst().get();
    }

    sentenceContent.setSubtense(subtense);
    if (subtense.getTense() == Tense.PAST) {
      sentenceContent.setNegation(false);
    }
    if (subtense != Subtense.IMPERATIVE) {
      wordListFr.add(new WordTypeWordTuple(WordType.VERB, abstractVerb.getVerbConjugation(abstractVerb, subject, subtense, Lang.FR), index));
      wordListAr.add(new WordTypeWordTuple(WordType.VERB, abstractVerb.getVerbConjugation(abstractVerb, subject, subtense, Lang.DZ), index));
    } else {
      PossessiveWord        randomPronoun = pronounService.getRandomImperativePersonalPronoun();
      Optional<Conjugation> frConjugation = abstractVerb.getImperativeVerbConjugation(randomPronoun, Lang.FR, sentenceContent.isNegation());
      wordListFr.add(new WordTypeWordTuple(WordType.VERB, frConjugation.get(), index));
      Optional<Conjugation> arConjugation = abstractVerb.getImperativeVerbConjugation(randomPronoun, Lang.DZ, sentenceContent.isNegation());
      wordListAr.add(new WordTypeWordTuple(WordType.VERB, arConjugation.get(), index));

    }
/*    if (schema.getFrSequence().contains(WordType.SUFFIX)) {
      Optional<PossessiveWord> suffixOpt;
      if (sentenceContent.getTense() == Tense.IMPERATIVE) {
        suffixOpt = helper.getImperativeSuffix(abstractVerb.isObjectOnly());
      } else {
        suffixOpt = helper.getSuffix(subject.getPossession(), abstractVerb.isObjectOnly());
      }
      if (suffixOpt.isEmpty()) {
        return false;
      }
      suffix = suffixOpt.get();
      wordListFr.add(new WordTypeWordTuple(WordType.SUFFIX, suffix, index));
      wordListAr.add(new WordTypeWordTuple(WordType.SUFFIX, suffix, index));
    }*/
    return true;
  }

  private boolean buildAdjective(int index) {
/*    Optional<Adjective> adjective = helper.getAbstractAdjective(abstractSubject, nounSubject);
    if (adjective.isEmpty()) {
      return false;
    }
    sentenceContent.setAbstractAdjective(adjective.get());
    wordListFr.add(new WordTypeWordTuple(WordType.ADJECTIVE, helper.getAdjective(adjective.get(), subject, Lang.FR).get(), index));
    wordListAr.add(new WordTypeWordTuple(WordType.ADJECTIVE, helper.getAdjective(adjective.get(), subject, Lang.DZ).get(), index));
    */
    return true;
  }

  private boolean builAdverb(int index) {
/*    AbstractWord adverb = helper.getAdverb();
    sentenceContent.setAbstractAdverb(adverb);
    wordListFr.add(new WordTypeWordTuple(WordType.ADVERB, (Word) adverb.getValues().get(0), index));
    wordListAr.add(new WordTypeWordTuple(WordType.ADVERB, (Word) adverb.getValues().get(0), index));
    */
    return true;
  }

  private boolean buildQuestion(int index) {
/*    abstractQuestion = helper.getQuestion();
    sentenceContent.setAbstractQuestion(abstractQuestion);
    Word question = (Word) abstractQuestion.getValues().get(0);
    wordListFr.add(new WordTypeWordTuple(WordType.QUESTION, question, index));
    wordListAr.add(new WordTypeWordTuple(WordType.QUESTION, question, index));
    */
    return true;
  }


  private Translation generateFrTranslation() {
    StringBuilder sentenceValue = new StringBuilder();
    for (int i = 0; i < schema.getFrSequence().size(); i++) {
      WordType wordType = schema.getFrSequence().get(i);
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
      if (w != null) {
        sentenceValue.append(w.getTranslationByLang(Lang.FR).get().getValue());
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
    for (int i = 0; i < schema.getArSequence().size(); i++) {
      WordType wordType = schema.getArSequence().get(i);
      if (wordType == WordType.SUFFIX) {
    /*    String suffixDzValue;
        sentenceValue.deleteCharAt(sentenceValue.length() - 1);
        sentenceValueAr.deleteCharAt(sentenceValueAr.length() - 1);
        if (getSentenceContent().getAbstractAdverb().isDzOppositeComplement()) {
          suffixDzValue = AbstractWord.getOppositeSuffix(suffix).getTranslationValue(lang);
          sentenceValueAr.append(AbstractWord.getOppositeSuffix(suffix).getTranslationByLang(Lang.DZ).get().getArValue());
        } else {
          suffixDzValue = getFirstWordFromWordTypeFr(wordType, i).getTranslationValue(lang);
          sentenceValueAr.append(getFirstWordFromWordTypeFr(wordType, i).getTranslationByLang(lang).get().getArValue());
        }
        sentenceValue.append(suffixDzValue);
        // manage transformation here iou -> ih, ouou->ou, etc.
        for (Entry<String, String> m : DB.RULE_MAP.entrySet()) {
          sentenceValue = new StringBuilder(sentenceValue.toString().replace(m.getKey(), m.getValue()));
        }*/
      } else {
        if (sentenceContent.isNegation() && sentenceContent.getSubtense().getTense() != Tense.PAST) {
          if (wordType == WordType.VERB) {
            sentenceValue.append("ma ");
            sentenceValueAr.append("ما ");
          } else if (wordType == WordType.ADJECTIVE
            //  && sentenceContent.getAbstractAdjective() != null && sentenceContent.getAbstractAdjective().isDefinitive()
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
//          if (sentenceContent.getAbstractAdjective() == null || sentenceContent.getAbstractAdjective().isTemporal()) {
          sentenceValue.append("ch ");
          sentenceValueAr.append("ش");
//          }
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
    if (schema.getFrSequence().contains(WordType.QUESTION)) {
      if (arabValue) {
        result += "؟";
      } else {
        result += "?";
      }
    }
    if (schema.getFrSequence().size() == 1 && schema.getFrSequence().get(0) == WordType.VERB) {
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
