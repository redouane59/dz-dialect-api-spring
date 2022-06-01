package io.github.dzdialectapispring.other.concrets;

import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GenderedWord extends Word {

  private Gender  gender; // to split between pronouns and noun which can change depending on the lang
  private boolean singular;

  public GenderedWord(List<Translation> translations, Gender gender, boolean singular) {
    super(translations);
    this.gender   = gender;
    this.singular = singular;
  }

  public Gender getGender(Lang lang) {
    if (this.getTranslationByLang(lang).isEmpty()) {
      return gender;
    }
    return this.getTranslationByLang(lang).get().getGender() != null
           ? this.getTranslationByLang(lang).get().getGender()
           : gender;
  }

  @Override
  public String toString() {
    String result = super.toString();
    result += " (" + this.gender + "/";
    if (this.isSingular()) {
      result += "sg";
    } else {
      result += "pl";
    }
    return result + ")";
  }

}