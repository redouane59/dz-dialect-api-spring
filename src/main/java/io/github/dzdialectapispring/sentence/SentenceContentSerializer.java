package io.github.dzdialectapispring.sentence;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.dzdialectapispring.sentence.Sentence.SentenceContent;
import java.io.IOException;

public class SentenceContentSerializer extends JsonSerializer<SentenceContent> {

  @Override
  public void serialize(final SentenceContent o, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
    ObjectNode node = OBJECT_MAPPER.createObjectNode();
    if (o == null) {
      return;
    }
    if (o.getSubtense() != null) {
      node.put("tense", o.getSubtense().getTense().getId());
    }
    if (o.getAbstractVerb() != null) {
      node.put("verb", o.getAbstractVerb().getId());
    }
    if (o.getAbstractPronoun() != null) {
      node.put("pronoun", o.getAbstractPronoun().getId());
    }
    jsonGenerator.writeObject(node);
  }
}
