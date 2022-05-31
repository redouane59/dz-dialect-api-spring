package io.github.dzdialectapispring.verb;

import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class VerbService {

  private final VerbRepository verbRepository;

  public Set<String> getAllVerbIds() {
    return verbRepository.findAll().stream().map(AbstractWord::getId).collect(Collectors.toSet());
  }

  public Verb getVerbById(final String id) {
    Optional<Verb> verbOptional = verbRepository.findById(id);
    if (verbOptional.isEmpty()) {
      throw new IllegalArgumentException("No verb found with id " + id);
    }
    return verbOptional.get();
  }
}
