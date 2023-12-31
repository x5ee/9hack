package software.bernie.shadowed.fasterxml.jackson.core.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import software.bernie.shadowed.fasterxml.jackson.core.JsonFactory;

public class DataFormatDetector {
   public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
   protected final JsonFactory[] _detectors;
   protected final MatchStrength _optimalMatch;
   protected final MatchStrength _minimalMatch;
   protected final int _maxInputLookahead;

   public DataFormatDetector(JsonFactory... detectors) {
      this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
   }

   public DataFormatDetector(Collection<JsonFactory> detectors) {
      this((JsonFactory[])detectors.toArray(new JsonFactory[detectors.size()]));
   }

   public DataFormatDetector withOptimalMatch(MatchStrength optMatch) {
      return optMatch == this._optimalMatch ? this : new DataFormatDetector(this._detectors, optMatch, this._minimalMatch, this._maxInputLookahead);
   }

   public DataFormatDetector withMinimalMatch(MatchStrength minMatch) {
      return minMatch == this._minimalMatch ? this : new DataFormatDetector(this._detectors, this._optimalMatch, minMatch, this._maxInputLookahead);
   }

   public DataFormatDetector withMaxInputLookahead(int lookaheadBytes) {
      return lookaheadBytes == this._maxInputLookahead ? this : new DataFormatDetector(this._detectors, this._optimalMatch, this._minimalMatch, lookaheadBytes);
   }

   private DataFormatDetector(JsonFactory[] detectors, MatchStrength optMatch, MatchStrength minMatch, int maxInputLookahead) {
      this._detectors = detectors;
      this._optimalMatch = optMatch;
      this._minimalMatch = minMatch;
      this._maxInputLookahead = maxInputLookahead;
   }

   public DataFormatMatcher findFormat(InputStream in) throws IOException {
      return this._findFormat(new InputAccessor.Std(in, new byte[this._maxInputLookahead]));
   }

   public DataFormatMatcher findFormat(byte[] fullInputData) throws IOException {
      return this._findFormat(new InputAccessor.Std(fullInputData));
   }

   public DataFormatMatcher findFormat(byte[] fullInputData, int offset, int len) throws IOException {
      return this._findFormat(new InputAccessor.Std(fullInputData, offset, len));
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      int len = this._detectors.length;
      if (len > 0) {
         sb.append(this._detectors[0].getFormatName());

         for(int i = 1; i < len; ++i) {
            sb.append(", ");
            sb.append(this._detectors[i].getFormatName());
         }
      }

      sb.append(']');
      return sb.toString();
   }

   private DataFormatMatcher _findFormat(InputAccessor.Std acc) throws IOException {
      JsonFactory bestMatch = null;
      MatchStrength bestMatchStrength = null;
      JsonFactory[] arr$ = this._detectors;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         JsonFactory f = arr$[i$];
         acc.reset();
         MatchStrength strength = f.hasFormat(acc);
         if (strength != null && strength.ordinal() >= this._minimalMatch.ordinal() && (bestMatch == null || bestMatchStrength.ordinal() < strength.ordinal())) {
            bestMatch = f;
            bestMatchStrength = strength;
            if (strength.ordinal() >= this._optimalMatch.ordinal()) {
               break;
            }
         }
      }

      return acc.createMatcher(bestMatch, bestMatchStrength);
   }
}
