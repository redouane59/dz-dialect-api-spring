package io.github.dzdialectapispring.verb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerbRepository extends MongoRepository<Verb, String> {

}
