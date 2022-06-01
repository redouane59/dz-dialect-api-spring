package io.github.dzdialectapispring.sentence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dzdialectapispring.other.enumerations.Lang;
import java.io.IOException;

public class SentenceSerializer extends StdSerializer<Sentence> {

  public SentenceSerializer() {
    this(null);
  }

  public SentenceSerializer(Class<Sentence> t) {
    super(t);
  }

  @Override
  public void serialize(
      Sentence sentence, JsonGenerator jgen, SerializerProvider provider)
  throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField(Lang.DZ.getId(), sentence.getTranslationByLang(Lang.DZ).get().getValue());
    jgen.writeStringField(Lang.DZ.getId() + "_ar", sentence.getTranslationByLang(Lang.DZ).get().getArValue());
    jgen.writeStringField(Lang.FR.getId(), sentence.getTranslationByLang(Lang.FR).get().getValue());
    // if (Config.SERIALIZE_ADDITIONAL_INFO) {
    if (!sentence.getAdditionalInformations().isEmpty()) {
      jgen.writeObjectField("additional_information", sentence.getAdditionalInformations());
    }
    /* }
    if (Config.SERIALIZE_WORD_PROPOSITIONS) {
      //fr
      jgen.writeFieldName("word_propositions_fr");
      jgen.writeStartArray();
      for (String s : sentence.getContent().getRandomFrWords()) {
        jgen.writeString(s);
      }
      jgen.writeEndArray();
      //ar
      jgen.writeFieldName("word_propositions_ar");
      jgen.writeStartArray();
      for (String s : sentence.getContent().getRandomArWords()) {
        jgen.writeString(s);
      }
      jgen.writeEndArray();
    }*/
    jgen.writeEndObject();
  }

}
