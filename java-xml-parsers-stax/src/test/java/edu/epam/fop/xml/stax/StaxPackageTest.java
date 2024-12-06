package edu.epam.fop.xml.stax;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceCamelCase.class)
class StaxPackageTest {

	private static final String JAVA_FILE = "src/main/java/edu/epam/fop/xml/stax/BreakfastMenuStaxParser.java";

	@ParameterizedTest
	@ValueSource(strings = { "javax.xml.stream" })
	void testStaxPackages(String packageName) {
		boolean packageFound = isPackageUsedInFile(JAVA_FILE, packageName);
		assertTrue(packageFound, "You have to use the " + packageName + " package to solve this task.");
	}

	@ParameterizedTest
	@ValueSource(strings = { "XMLInputFactory", "XMLStreamReader" })
	void testStaxClasses(String className) {
		boolean classFound = isClassUsedInFile(JAVA_FILE, className);
		assertTrue(classFound, "You have to use the " + className + " class to solve this task.");
	}

	private boolean isPackageUsedInFile(String filePath, String packageName) {
		List<String> lines = readFile(filePath);
		Pattern pattern = Pattern.compile("import\\s+" + Pattern.quote(packageName) + "\\.[^;]+;");
		return lines.stream().anyMatch(line -> pattern.matcher(line).find());
	}

	private boolean isClassUsedInFile(String filePath, String className) {
		List<String> lines = readFile(filePath);
		Pattern pattern = Pattern.compile("\\s" + Pattern.quote(className) + "\\s");
		return lines.stream().anyMatch(line -> pattern.matcher(line).find());
	}

	private List<String> readFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			return Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + filePath, e);
		}
	}
}
