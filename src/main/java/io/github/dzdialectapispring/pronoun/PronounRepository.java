package io.github.dzdialectapispring.pronoun;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PronounRepository extends MongoRepository<Pronoun, String> {

}
