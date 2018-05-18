package io.github.martinschneider.cucumber2junit;

import org.apache.commons.lang.WordUtils;

/**
 * This class contains helper methods to convert arbitrary Strings to valid Java method and class
 * names which also follow the standard camel-case naming convention.
 */
public class JavaUtils {

  public static String toClassName(String name) {
    return JavaUtils.sanitiseForJava(WordUtils.capitalizeFully(name).replaceAll(" ", ""));
  }

  public static String toMethodName(String name) {
    return JavaUtils.sanitiseForJava(
        WordUtils.uncapitalize(WordUtils.capitalizeFully(name).replaceAll(" ", "")));
  }

  public static String sanitiseForJava(String javaName) {
    StringBuilder stringBuilder = new StringBuilder();
    for (char c : javaName.toCharArray()) {
      if (Character.isJavaIdentifierPart(c)) {
        stringBuilder.append(c);
      }
    }
    return stringBuilder.toString();
  }
}
