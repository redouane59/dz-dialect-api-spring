package io.github.dzdialectapispring.verb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.dzdialectapispring.other.NounType;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import io.github.dzdialectapispring.other.concrets.Possession;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import io.github.dzdialectapispring.other.enumerations.RootLang;
import io.github.dzdialectapispring.other.enumerations.Subtense;
import io.github.dzdialectapispring.other.enumerations.Tense;
import io.github.dzdialectapispring.other.enumerations.WordType;
import io.github.dzdialectapispring.verb.conjugation.Conjugation;
import io.github.dzdialectapispring.verb.conjugation.ConjugationListDeserializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_DEFAULT)
// @todo reflexive verbs (ça me plaît, il me faut, etc.)
public class Verb extends AbstractWord {

  @JsonInclude(Include.NON_EMPTY)
  @JsonDeserialize(using = ConjugationListDeserializer.class)
  private final List<Conjugation> values = new ArrayList<>();

  @JsonProperty("possible_questions")
  private List<String>   possibleQuestionIds = new ArrayList<>();
  @JsonProperty("possible_complements")
  private List<NounType> possibleComplements = new ArrayList<>(); // @todo add verbs for PVV/NVV sentences
  @JsonProperty("indirect_complement")
  private boolean        indirectComplement; // ex: je LUI donne quelque chose.
  @JsonProperty("direct_complement")
  private boolean        directComplement; // ex: je LE donne.
  @JsonProperty("dz_opposite_complement")
  private boolean        dzOppositeComplement; // ex: je l'appelle / n3ayetlou
  @JsonProperty("object_only")
  private boolean        objectOnly; // true : verbe ouvrir
  @JsonProperty("dz_no_suffix")
  private boolean        dzNoSuffix;
  @JsonProperty("semi_auxiliar")
  private boolean        semiAuxiliar; // conjugated verb + infinitive verb
  private WordType       wordType            = WordType.VERB;
  @JsonProperty("verb_type")
  private VerbType       verbType;

  public Optional<Conjugation> getConjugationByGenderSingularPossessionAndTense(Gender gender,
                                                                                boolean isSingular,
                                                                                Possession possession,
                                                                                Tense tense) {
    return getValues().stream()
                      .filter(o -> o.getSubtense().getTense() == tense)
                      .filter(o -> o.isSingular() == isSingular)
                      .filter(o -> o.getPossession() == possession)
                      .filter(o -> o.getGender() == gender || gender == Gender.X || o.getGender() == Gender.X)
                      .findAny();

  }

  public Conjugation getVerbConjugation(Verb verb, PossessiveWord subject, Subtense tense, Lang lang) {

/*    if (subject == null) {
      subject = pronounService.getRandomPronoun();
      tense   = Tense.IMPERATIVE;
    }*/
    Optional<Conjugation>
        conjugation =
        verb.getConjugationByGenderSingularPossessionAndTense(subject.getGender(lang),
                                                              subject.isSingular(),
                                                              subject.getPossession(),
                                                              tense.getTense());
    if (conjugation.isEmpty()) {
      System.err.println("no conjugation found for");
      return null;
    }
    return conjugation.get();
  }

  // to manage the fact that two files are needed to import a verb (see DB.java)
  public void importConfig(Verb other) {
    this.possibleQuestionIds  = other.getPossibleQuestionIds();
    this.possibleComplements  = other.getPossibleComplements();
    this.verbType             = other.getVerbType();
    this.indirectComplement   = other.isIndirectComplement();
    this.directComplement     = other.isDirectComplement();
    this.dzOppositeComplement = other.isDzOppositeComplement();
    this.objectOnly           = other.isObjectOnly();
    this.dzNoSuffix           = other.isDzNoSuffix();
    this.semiAuxiliar         = other.isSemiAuxiliar();
  }

  public Optional<Conjugation> getImperativeVerbConjugation(final PossessiveWord subject, final Lang lang, final boolean isNegative) {
    Tense tense;
    if (isNegative && lang.getRootLang() == RootLang.AR) {
      tense = Tense.PRESENT; // to manage exception in arabic
    } else {
      tense = Tense.IMPERATIVE;
    }
    return this.getConjugationByGenderSingularPossessionAndTense(subject.getGender(lang),
                                                                 subject.isSingular(),
                                                                 subject.getPossession(),
                                                                 tense);
  }
}