package io.github.dzdialectapispring.sentence;

import org.springframework.data.mongodb.repository.MongoRepository;

  public interface SentenceRepository
      extends MongoRepository<Sentence, String> {

}
