package software.bernie.shadowed.fasterxml.jackson.databind.cfg;

public interface ConfigFeature {
   boolean enabledByDefault();

   int getMask();

   boolean enabledIn(int var1);
}
