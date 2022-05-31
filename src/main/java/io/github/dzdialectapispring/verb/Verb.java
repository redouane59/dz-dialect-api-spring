package io.github.dzdialectapispring.verb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.dzdialectapispring.other.abstracts.AbstractWord;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_DEFAULT)
// @todo reflexive verbs (ça me plaît, il me faut, etc.)
public class Verb extends AbstractWord {

}