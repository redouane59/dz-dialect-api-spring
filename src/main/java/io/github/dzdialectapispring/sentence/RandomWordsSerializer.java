package io.github.dzdialectapispring.sentence;

import static io.github.dzdialectapispring.other.Config.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RandomWordsSerializer extends JsonSerializer<Map<Lang, List<String>>> {

  @Override
  public void serialize(final Map<Lang, List<String>> o, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
  throws IOException {
    ObjectNode wordPropositions = OBJECT_MAPPER.createObjectNode();
    if (o != null && !o.isEmpty()) {
      for (Entry<Lang, List<String>> s : o.entrySet()) {
        String       langId = s.getKey().getId();
        List<String> word   = s.getValue();
        wordPropositions.putIfAbsent(langId, OBJECT_MAPPER.valueToTree(word));
      }
    }
    jsonGenerator.writeObject(wordPropositions);
  }

  @Override
  public boolean isEmpty(final SerializerProvider provider, final Map<Lang, List<String>> value) {
    return value.isEmpty();
  }
}
