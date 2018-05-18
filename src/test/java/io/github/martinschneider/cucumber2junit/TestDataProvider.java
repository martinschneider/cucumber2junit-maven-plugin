package io.github.martinschneider.cucumber2junit;

import io.github.martinschneider.cucumber2junit.model.Feature;
import io.github.martinschneider.cucumber2junit.model.Scenario;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestDataProvider {

  public static Feature getFeature1() {
    Scenario scenario1 =
        new Scenario(4, "Ask a question using quick link", Collections.emptyList());
    return new Feature(
        "New question",
        "stackoverflow/NewQuestion.feature",
        Stream.of(scenario1).collect(Collectors.toList()),
        Stream.of("android").collect(Collectors.toList()));
  }

  public static Feature getFeature2() {
    Scenario scenario2a =
        new Scenario(4, "Filter by tags", Stream.of("web").collect(Collectors.toList()));
    Scenario scenario2b =
        new Scenario(
            17,
            "Use the search function",
            Stream.of("web", "android", "ios").collect(Collectors.toList()));
    return new Feature(
        "Search and tags",
        "stackoverflow/Search.feature",
        Stream.of(scenario2a, scenario2b).collect(Collectors.toList()),
        Collections.emptyList());
  }
  
  public static Feature getFeature2IosOnly() {
    Scenario scenario2 =
        new Scenario(
            17,
            "Use the search function",
            Stream.of("web", "android", "ios").collect(Collectors.toList()));
    return new Feature(
        "Search and tags",
        "stackoverflow/Search.feature",
        Stream.of(scenario2).collect(Collectors.toList()),
        Collections.emptyList());
  }

  public static Feature getFeature3() {
    Scenario scenario3 = new Scenario(4, "Successful login", Collections.emptyList());
    return new Feature(
        "Login",
        "carousell/Login.feature",
        Stream.of(scenario3).collect(Collectors.toList()),
        Stream.of("web", "android", "ios").collect(Collectors.toList()));
  }
}
