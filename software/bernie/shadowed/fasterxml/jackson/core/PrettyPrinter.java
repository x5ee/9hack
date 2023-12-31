package software.bernie.shadowed.fasterxml.jackson.core;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.core.io.SerializedString;
import software.bernie.shadowed.fasterxml.jackson.core.util.Separators;

public interface PrettyPrinter {
   Separators DEFAULT_SEPARATORS = Separators.createDefaultInstance();
   SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");

   void writeRootValueSeparator(JsonGenerator var1) throws IOException;

   void writeStartObject(JsonGenerator var1) throws IOException;

   void writeEndObject(JsonGenerator var1, int var2) throws IOException;

   void writeObjectEntrySeparator(JsonGenerator var1) throws IOException;

   void writeObjectFieldValueSeparator(JsonGenerator var1) throws IOException;

   void writeStartArray(JsonGenerator var1) throws IOException;

   void writeEndArray(JsonGenerator var1, int var2) throws IOException;

   void writeArrayValueSeparator(JsonGenerator var1) throws IOException;

   void beforeArrayValues(JsonGenerator var1) throws IOException;

   void beforeObjectEntries(JsonGenerator var1) throws IOException;
}
