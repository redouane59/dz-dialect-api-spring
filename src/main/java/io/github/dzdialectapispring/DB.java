package io.github.dzdialectapispring;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import io.github.dzdialectapispring.adjective.Adjective;
import io.github.dzdialectapispring.adverb.adjective.Adverb;
import io.github.dzdialectapispring.generic.ResourceList;
import io.github.dzdialectapispring.noun.Noun;
import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.abstracts.DefinedArticles;
import io.github.dzdialectapispring.pronoun.AbstractPronoun;
import io.github.dzdialectapispring.question.AbstractQuestion;
import io.github.dzdialectapispring.sentence.SentenceSchema;
import io.github.dzdialectapispring.verb.Verb;
import io.github.dzdialectapispring.verb.suffix.AbstractSuffix;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DB {

  public final static Set<Verb>              VERBS             = new HashSet<>();
  public final static Set<Adjective>         ADJECTIVES        = new HashSet<>();
  public final static Set<Noun>              NOUNS             = new HashSet<>();
  public final static Set<Adverb>            ADVERBS           = new HashSet<>();
  public final static Set<SentenceSchema>    SENTENCE_SCHEMAS  = new HashSet<>();
  public final static List<AbstractQuestion> QUESTIONS         = new ArrayList<>();
  public final static List<AbstractPronoun>  PERSONAL_PRONOUNS = new ArrayList<>();
  public static       DefinedArticles        DEFINED_ARTICLES;
  public static       AbstractWord           UNDEFINED_ARTICLES;
  public static       AbstractWord           POSSESSIVE_ARTICLES;
  public static       AbstractSuffix         DIRECT_SUFFIXES;
  public static       AbstractSuffix         INDIRECT_SUFFIXES;

  public static void init() {
    initAdjectives();
    initAdverbs();
    initNouns();
    initDefinedArticles();
    initDirectSuffixes();
    initIndirectSuffixes();
    initPersonalPronouns();
    initQuestions();
    initSentenceSchemas();
    initVerbs();
  }

  public static void initDirectSuffixes() {
    try {
      DIRECT_SUFFIXES = Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/suffixes/direct_pronoun_suffixes.json"),
                                                       AbstractSuffix.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("direct suffixes loaded");

  }

  public static void initIndirectSuffixes() {
    try {
      INDIRECT_SUFFIXES =
          Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/suffixes/indirect_pronoun_suffixes.json"),
                                         AbstractSuffix.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("indirect suffixes loaded");
  }

  public static void initDefinedArticles() {
    try {
      DEFINED_ARTICLES =
          Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/articles/defined_articles.json"), DefinedArticles.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("defined articles loaded");
  }

  public static void initPossessiveArticles() {
    try {
      POSSESSIVE_ARTICLES =
          Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/articles/possessive_articles_singular.json"), AbstractWord.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(POSSESSIVE_ARTICLES + " possessive articles loaded");
  }

  public static void initUndefinedArticles() {
    try {
      UNDEFINED_ARTICLES =
          Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/articles/undefined_articles.json"), AbstractWord.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(UNDEFINED_ARTICLES + " undefined articles loaded");
  }

  public static void initVerbs() {
    Verb[] verbConfigurations = new Verb[]{};
    try {
      verbConfigurations =
          OBJECT_MAPPER.readValue(new File("./src/main/resources/static/verbs/.verb_config.json"), Verb[].class);
    } catch (Exception e) {
      System.err.println("could not load verb configurations " + e.getMessage());
      e.printStackTrace();
    }
    Set<String> files = new HashSet<>(ResourceList.getResources(Pattern.compile(".*verbs.*json")))
        .stream().filter(o -> !o.contains(".verb_config.json")).collect(Collectors.toSet());
    for (String fileName : files) {
      Verb verb;
      try {
        verb = OBJECT_MAPPER.readValue(new File(fileName), Verb.class);
        Optional<Verb> verbConfiguration = Arrays.stream(verbConfigurations).filter(o -> o.getId().equals(verb.getId())).findFirst();
        if (verbConfiguration.isPresent()) {
          verb.importConfig(verbConfiguration.get());
        } else {
          System.out.println("no configuration found for verb " + verb.getId());
        }
        VERBS.add(verb);
      } catch (IOException e) {
        System.err.println("could not load verb  " + e.getMessage());
        e.printStackTrace();
      }
    }
    System.out.println(VERBS + " verbs loaded");
  }

  public static void initAdjectives() {
    Adjective[] adjectiveConfigurations = new Adjective[]{};
    try {
      adjectiveConfigurations =
          OBJECT_MAPPER.readValue(new File("./src/main/resources/static/adjectives/.adjective_config.json"), Adjective[].class);
    } catch (Exception e) {
      System.err.println("could not load adjective configurations " + e.getMessage());
      e.printStackTrace();
    }
    Set<String>
        files =
        new HashSet<>(ResourceList.getResources(Pattern.compile(".*adjectives.*json")))
            .stream().filter(o -> !o.contains(".adjective_config.json")).collect(Collectors.toSet());
    for (String fileName : files) {
      try {
        Adjective adjective = OBJECT_MAPPER.readValue(new File(fileName), Adjective.class);
        Optional<Adjective>
            adjectiveConfiguration =
            Arrays.stream(adjectiveConfigurations).filter(o -> o.getId().equals(adjective.getId())).findFirst();
        if (adjectiveConfiguration.isPresent()) {
          adjective.importConfig(adjectiveConfiguration.get());
        } else {
          System.out.println("no configuration found for adjective " + adjective.getId());
        }
        ADJECTIVES.add(adjective);
      } catch (IOException e) {
        System.err.println("could not load adjective file " + fileName);
        e.printStackTrace();
      }
    }
    System.out.println(ADJECTIVES.size() + " adjectives loaded");
  }

  public static void initNouns() {
    Set<String>
        files =
        new HashSet<>(ResourceList.getResources(Pattern.compile(".*nouns.*json")));
    for (String fileName : files) {
      try {
        if (!fileName.contains("pronoun")) {
          Noun noun = OBJECT_MAPPER.readValue(new File(fileName), Noun.class);
          NOUNS.add(noun);
        }
      } catch (IOException e) {
        System.err.println("could not load noun file " + fileName);
        e.printStackTrace();
      }
    }
    System.out.println(NOUNS.size() + " nouns loaded");
  }

  public static void initAdverbs() {
    Set<String>
        files =
        new HashSet<>(ResourceList.getResources(Pattern.compile(".*adv.*json")));
    for (String fileName : files) {
      try {
        Adverb adverb = OBJECT_MAPPER.readValue(new File(fileName), Adverb.class);
        ADVERBS.add(adverb);
      } catch (IOException e) {
        System.err.println("could not load adverb file " + fileName);
        e.printStackTrace();
      }
    }
    System.out.println(ADVERBS.size() + " adverbs loaded");
  }

  public static void initSentenceSchemas() {
    Set<String> files = new HashSet<>(ResourceList.getResources(Pattern.compile(".*sentence_schemas.*json")));
    for (String fileName : files) {
      try {
        SENTENCE_SCHEMAS.add(Config.OBJECT_MAPPER.readValue(new File(fileName), SentenceSchema.class));
      } catch (IOException e) {
        System.err.println("could not load sentence schema file " + fileName);
        e.printStackTrace();
      }
    }
    System.out.println(SENTENCE_SCHEMAS.size() + " sentences builders loaded");
  }

  public static void initQuestions() {
    try {
      QUESTIONS.addAll(List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/questions.json"), AbstractQuestion[].class)));
    } catch (IOException e) {
      System.err.println("questions not loaded " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println(QUESTIONS.size() + " questions loaded");
  }

  public static void initPersonalPronouns() {
    try {
      PERSONAL_PRONOUNS.addAll(List.of(OBJECT_MAPPER.readValue(new File("./src/main/resources/static/other/personal_pronouns.json"),
                                                               AbstractPronoun[].class)));
    } catch (IOException e) {
      System.err.println("cannot load personal pronouns " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println(PERSONAL_PRONOUNS.size() + " personal pronouns loaded");
  }


}
