package io.github.dzdialectapispring.other.abstracts;

import io.github.dzdialectapispring.DB;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter

public class DefinedArticles extends AbstractWord {

  private List<GenderedWord> values;

  // @todo dirty
  public static Optional<GenderedWord> getDefinedArticleByCriterion(Gender gender, boolean singular) {
    return DB.DEFINED_ARTICLES.getValues().stream()
                              .filter(a -> a.isSingular() == singular)
                              .filter(a -> a.getGender() == gender)
                              .findAny();
  }

  public static Optional<GenderedWord> getArticle(PossessiveWord noun, Lang lang) {
    Optional<GenderedWord> article = getDefinedArticleByCriterion(noun.getGender(lang), noun.isSingular());
    if (article.isEmpty()) {
      System.out.println("empty article");
      return Optional.empty();
    }
    return article;
  }

}
