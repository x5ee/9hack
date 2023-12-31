package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public class TypeParser implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final TypeFactory _factory;

   public TypeParser(TypeFactory f) {
      this._factory = f;
   }

   public TypeParser withFactory(TypeFactory f) {
      return f == this._factory ? this : new TypeParser(f);
   }

   public JavaType parse(String canonical) throws IllegalArgumentException {
      canonical = canonical.trim();
      TypeParser.MyTokenizer tokens = new TypeParser.MyTokenizer(canonical);
      JavaType type = this.parseType(tokens);
      if (tokens.hasMoreTokens()) {
         throw this._problem(tokens, "Unexpected tokens after complete type");
      } else {
         return type;
      }
   }

   protected JavaType parseType(TypeParser.MyTokenizer tokens) throws IllegalArgumentException {
      if (!tokens.hasMoreTokens()) {
         throw this._problem(tokens, "Unexpected end-of-string");
      } else {
         Class<?> base = this.findClass(tokens.nextToken(), tokens);
         if (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ("<".equals(token)) {
               List<JavaType> parameterTypes = this.parseTypes(tokens);
               TypeBindings b = TypeBindings.create(base, parameterTypes);
               return this._factory._fromClass((ClassStack)null, base, b);
            }

            tokens.pushBack(token);
         }

         return this._factory._fromClass((ClassStack)null, base, (TypeBindings)null);
      }
   }

   protected List<JavaType> parseTypes(TypeParser.MyTokenizer tokens) throws IllegalArgumentException {
      ArrayList types = new ArrayList();

      while(tokens.hasMoreTokens()) {
         types.add(this.parseType(tokens));
         if (!tokens.hasMoreTokens()) {
            break;
         }

         String token = tokens.nextToken();
         if (">".equals(token)) {
            return types;
         }

         if (!",".equals(token)) {
            throw this._problem(tokens, "Unexpected token '" + token + "', expected ',' or '>')");
         }
      }

      throw this._problem(tokens, "Unexpected end-of-string");
   }

   protected Class<?> findClass(String className, TypeParser.MyTokenizer tokens) {
      try {
         return this._factory.findClass(className);
      } catch (Exception var4) {
         ClassUtil.throwIfRTE(var4);
         throw this._problem(tokens, "Cannot locate class '" + className + "', problem: " + var4.getMessage());
      }
   }

   protected IllegalArgumentException _problem(TypeParser.MyTokenizer tokens, String msg) {
      return new IllegalArgumentException("Failed to parse type '" + tokens.getAllInput() + "' (remaining: '" + tokens.getRemainingInput() + "'): " + msg);
   }

   static final class MyTokenizer extends StringTokenizer {
      protected final String _input;
      protected int _index;
      protected String _pushbackToken;

      public MyTokenizer(String str) {
         super(str, "<,>", true);
         this._input = str;
      }

      public boolean hasMoreTokens() {
         return this._pushbackToken != null || super.hasMoreTokens();
      }

      public String nextToken() {
         String token;
         if (this._pushbackToken != null) {
            token = this._pushbackToken;
            this._pushbackToken = null;
         } else {
            token = super.nextToken();
         }

         this._index += token.length();
         return token;
      }

      public void pushBack(String token) {
         this._pushbackToken = token;
         this._index -= token.length();
      }

      public String getAllInput() {
         return this._input;
      }

      public String getUsedInput() {
         return this._input.substring(0, this._index);
      }

      public String getRemainingInput() {
         return this._input.substring(this._index);
      }
   }
}
