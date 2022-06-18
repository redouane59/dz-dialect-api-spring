package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.dzdialectapispring.other.concrets.Translation;
import io.github.dzdialectapispring.other.concrets.Word;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class WordDTO {

  @JsonProperty("dz")
  private String dz;
  @JsonProperty("dz_ar")
  private String dzAr;
  @JsonProperty("fr")
  private String fr;

  public WordDTO(Word word) {
    this.dz   = word.getDzTranslationValue();
    this.dzAr = word.getDzTranslationValueAr();
    this.fr   = word.getFrTranslationValue();
  }

  public WordDTO(List<Translation> translations) {
    Optional<Translation> dzOpt = translations.stream().filter(t -> t.getLang() == Lang.DZ).findFirst();
    if (dzOpt.isPresent()) {
      this.dz   = dzOpt.get().getValue();
      this.dzAr = dzOpt.get().getArValue();
    }
    Optional<Translation> frOpt = translations.stream().filter(t -> t.getLang() == Lang.FR).findFirst();
    if (frOpt.isPresent()) {
      this.fr = frOpt.get().getValue();
    }
  }

}
