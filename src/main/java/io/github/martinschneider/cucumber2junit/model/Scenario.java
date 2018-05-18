package io.github.martinschneider.cucumber2junit.model;

import com.google.common.base.Objects;
import io.github.martinschneider.cucumber2junit.JavaUtils;
import java.util.List;

/** DTO for Gherkin scenarios. */
public class Scenario {

  private int lineNumber;
  private String name;
  private List<String> tags;

  /**
   * Constructor.
   *
   * @param lineNumber line number of the scenario
   * @param name scenario name
   * @param tags {@link List} of tags
   */
  public Scenario(int lineNumber, String name, List<String> tags) {
    super();
    this.lineNumber = lineNumber;
    this.name = name;
    this.tags = tags;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(name, lineNumber, tags);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Scenario other = (Scenario) obj;
    return Objects.equal(this.name, other.name)
        && Objects.equal(this.lineNumber, other.lineNumber)
        && Objects.equal(this.tags, other.tags);
  }
  
  public String getJavaName() {
    return JavaUtils.toMethodName(name);
  }
}
