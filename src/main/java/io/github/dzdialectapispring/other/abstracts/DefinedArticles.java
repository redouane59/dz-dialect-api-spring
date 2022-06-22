package io.github.dzdialectapispring.other.abstracts;

import io.github.dzdialectapispring.other.Config;
import io.github.dzdialectapispring.other.concrets.GenderedWord;
import io.github.dzdialectapispring.other.concrets.PossessiveWord;
import io.github.dzdialectapispring.other.enumerations.Gender;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DefinedArticles extends AbstractWord {

  private List<GenderedWord> values;

  // @todo dirty
  public static Optional<GenderedWord> getDefinedArticleByCriterion(Gender gender, boolean singular) {
    DefinedArticles articles;
    try {
      articles = Config.OBJECT_MAPPER.readValue(new File("./src/main/resources/static/articles/defined_articles.json"), DefinedArticles.class);
      return articles.getValues().stream()
                     .filter(a -> a.isSingular() == singular)
                     .filter(a -> a.getGender() == gender)
                     .findAny();
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      return Optional.empty();
    }
  }

  public static Optional<GenderedWord> getArticle(PossessiveWord noun, Lang lang) {
    Optional<GenderedWord> article = getDefinedArticleByCriterion(noun.getGender(lang), noun.isSingular());
    if (article.isEmpty()) {
      LOGGER.debug("empty article");
      return Optional.empty();
    }
    return article;
  }

}
