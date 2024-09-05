package edu.epam.rd.autotasks.maven;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@DisplayName("Tests the correctness of the POM file inside project directory")
public class PomTest {

  private static Model model;

  @BeforeAll
  static void setUpClass() {
    try {
      model = new MavenXpp3Reader().read(new FileReader(Path.of("project", "pom.xml").toFile()));
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Can't find POM file of the project", e);
    } catch (XmlPullParserException e) {
      throw new IllegalStateException("POM file is invalid XML", e);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read POM", e);
    }
  }

  @Test
  @DisplayName("Tests that POM has a correct model version")
  void modelVersion() {
    assertEquals("4.0.0", model.getModelVersion());
  }

  @Test
  @DisplayName("Tests that project has a correct coordinates (groupId, artifactId, version)")
  void artifactCoordinates() {
    assertAll(
        () -> assertEquals("com.vanderlinde", model.getGroupId()),
        () -> assertEquals("awesome-plan", model.getArtifactId()),
        () -> assertEquals("0.0.1-SNAPSHOT", model.getVersion())
    );
  }

  @Test
  @DisplayName("Tests that the project has an expected list of developers")
  void developers() {
    List<Developer> developers = model.getDevelopers();
    developers.sort(Comparator.comparing(Developer::getId));
    BiConsumer<Developer, Developer> asserter = complexAsserter(
        Developer::getId,
        Developer::getName,
        Developer::getEmail,
        Developer::getOrganization,
        Developer::getTimezone);
    assertComplexList(developers, asserter,
        new Developer() {{
          setId("johnnyboy");
          setName("John Marston");
          setEmail("johnmarston@linde.der");
          setOrganization("Van der Linde");
          setTimezone("-7");
        }},
        new Developer() {{
          setId("marshall");
          setName("Leigh Johnson");
          setEmail("leighjohnson@armadillo.com");
          setOrganization("Town of Armadillo");
          setTimezone("-7");
        }});
  }

  @Test
  @DisplayName("Tests that the project has an expected list of dependencies")
  void dependencies() {
    List<Dependency> dependencies = model.getDependencies();
    dependencies.sort(Comparator.comparing(Dependency::getArtifactId));
    BiConsumer<Dependency, Dependency> asserter = complexAsserter(
        Dependency::getArtifactId,
        Dependency::getGroupId,
        Dependency::getVersion,
        Dependency::getScope
    );
    assertComplexList(dependencies, asserter,
        new Dependency() {{
          setArtifactId("commons-lang3");
          setGroupId("org.apache.commons");
          setVersion("3.12.0");
        }},
        new Dependency() {{
          setArtifactId("junit-jupiter");
          setGroupId("org.junit.jupiter");
          setVersion("5.7.2");
          setScope("test");
        }},
        new Dependency() {{
          setArtifactId("lombok");
          setGroupId("org.projectlombok");
          setVersion("1.18.20");
          setScope("provided");
        }});
  }

  private <T> void assertComplexList(List<T> list, BiConsumer<T, T> asserter, T... expectedElements) {
    assertEquals(expectedElements.length, list.size());
    Stream<Executable> executables = IntStream.range(0, expectedElements.length)
        .mapToObj(i -> () -> asserter.accept(expectedElements[i], list.get(i)));
    assertAll(executables);
  }

  @SafeVarargs
  private <T> BiConsumer<T, T> complexAsserter(Function<T, ?>... getters) {
    return (expected, actual) -> {
      Stream<Executable> executables = Arrays.stream(getters)
          .map(getter -> () -> assertEquals(getter.apply(expected), getter.apply(actual)));
      assertAll(executables);
    };
  }
}
