# Guía de Testing para GeneradorFichero.java

Esta guía explica cómo escribir tests unitarios para todos los métodos de la clase `GeneradorFichero.java`.

## Índice
1. [Configuración inicial](#configuración-inicial)
2. [Estrategias de testing](#estrategias-de-testing)
3. [Tests por método](#tests-por-método)
4. [Buenas prácticas](#buenas-prácticas)

---

## Configuración inicial

### Dependencias necesarias (Maven)

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Mockito para mocking (opcional pero recomendado) -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JUnit Pioneer para testing de System.in/out -->
    <dependency>
        <groupId>org.junit-pioneer</groupId>
        <artifactId>junit-pioneer</artifactId>
        <version>2.1.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Estrategias de testing

### Métodos privados

Los métodos privados en `GeneradorFichero.java` (`isGreater`, `getDataFromMain`, `generateFile`) tienen dos enfoques:

1. **Opción recomendada**: Testearlos indirectamente a través del método público `main()`.
2. **Opción alternativa**: Cambiar la visibilidad a `package-private` o usar reflection (no recomendado).

Para esta guía, usaremos ambos enfoques donde sea apropiado.

---

## Tests por método

### 1. Testear `isGreater(int value, int threshold)`

#### Problema
Es un método privado.

#### Solución
**Opción A: Hacer el método package-private** (cambiar `private` a sin modificador)

```java
// En GeneradorFichero.java
static boolean isGreater(int value, int threshold) {
    return value >= threshold;
}
```

**Test correspondiente:**

```java
package es.dm2e.psp.actividad01.grupo2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeneradorFicheroTest {

    @Test
    void testIsGreater_ValueGreaterThanThreshold() {
        assertTrue(GeneradorFichero.isGreater(50, 30));
    }

    @Test
    void testIsGreater_ValueEqualToThreshold() {
        assertTrue(GeneradorFichero.isGreater(30, 30));
    }

    @Test
    void testIsGreater_ValueLessThanThreshold() {
        assertFalse(GeneradorFichero.isGreater(20, 30));
    }

    @Test
    void testIsGreater_NegativeNumbers() {
        assertTrue(GeneradorFichero.isGreater(-5, -10));
        assertFalse(GeneradorFichero.isGreater(-15, -10));
    }

    @Test
    void testIsGreater_Zero() {
        assertTrue(GeneradorFichero.isGreater(0, 0));
        assertFalse(GeneradorFichero.isGreater(-1, 0));
    }
}
```

**Opción B: Usar Reflection** (no recomendado, solo para casos excepcionales)

```java
@Test
void testIsGreaterUsingReflection() throws Exception {
    Method method = GeneradorFichero.class.getDeclaredMethod("isGreater", int.class, int.class);
    method.setAccessible(true);

    boolean result = (boolean) method.invoke(null, 50, 30);
    assertTrue(result);
}
```

---

### 2. Testear `main(String[] args)`

#### Problema
- Usa `System.exit()` que termina la JVM.
- Usa `randomInt()` que genera valores aleatorios.
- Llama a `getDataFromMain()` que lee de `System.in`.

#### Solución
Usar `SecurityManager` (deprecated pero funcional) o **System Lambda/JUnit Pioneer**.

**Usando System Lambda:**

```xml
<!-- Añadir dependencia -->
<dependency>
    <groupId>com.github.stefanbirkner</groupId>
    <artifactId>system-lambda</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

```java
import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class GeneradorFicheroMainTest {

    @TempDir
    Path tempDir;

    @Test
    void testMain_SuccessfulExecution() throws Exception {
        // Preparar input simulado
        String input = tempDir.toString() + "\n" +
                      "transferencias.txt\n" +
                      "10\n";

        // Capturar el exit status
        int status = catchSystemExit(() -> {
            withTextFromSystemIn(input.split("\n"))
                .execute(() -> GeneradorFichero.main(new String[]{}));
        });

        // Verificar que salió con status 0 o 100
        assertTrue(status == 0 || status == 100);
    }

    @Test
    void testMain_RandomFailure() throws Exception {
        // Este test puede fallar aleatoriamente debido al random
        // Mejor testear la lógica de isGreater por separado
        int exitCode = catchSystemExit(() ->
            GeneradorFichero.main(new String[]{})
        );

        assertTrue(exitCode == 0 || exitCode == 100 || exitCode == 200);
    }
}
```

**Nota**: Testear `main()` directamente es complejo debido a la aleatoriedad. Es mejor refactorizar para inyectar dependencias.

---

### 3. Testear `getDataFromMain()`

#### Problema
- Es privado.
- Lee de `System.in`.
- Usa `System.exit()`.

#### Solución
**Opción recomendada: Refactorizar** para hacerlo testeable.

**Estado actual:**
```java
private static void getDataFromMain(){
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
        // ...
    }
}
```

**Refactorización sugerida:**
```java
// Hacer package-private y aceptar un InputStream
static void getDataFromInput(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String directoryName = br.readLine();
        String fileName = br.readLine();
        Path completePath = Path.of(directoryName, fileName);
        int transferencias = Integer.parseInt(br.readLine());
        generateFile(Paths.get(completePath.toUri()), transferencias);
    }
}
```

**Test correspondiente:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class GeneradorFicheroInputTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetDataFromInput_ValidInput() throws IOException {
        // Preparar input simulado
        String input = tempDir.toString() + "\n" +
                      "test_transferencias.txt\n" +
                      "5\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        // Ejecutar (asumiendo refactorización)
        GeneradorFichero.getDataFromInput(inputStream);

        // Verificar que el archivo fue creado
        Path expectedFile = tempDir.resolve("test_transferencias.txt");
        assertTrue(Files.exists(expectedFile));
    }

    @Test
    void testGetDataFromInput_InvalidNumber() {
        String input = tempDir.toString() + "\n" +
                      "test.txt\n" +
                      "no_es_numero\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        assertThrows(NumberFormatException.class, () -> {
            GeneradorFichero.getDataFromInput(inputStream);
        });
    }

    @Test
    void testGetDataFromInput_EmptyInput() {
        String input = "\n\n\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        assertThrows(IOException.class, () -> {
            GeneradorFichero.getDataFromInput(inputStream);
        });
    }
}
```

---

### 4. Testear `generateFile(Path path, int nTransfers)`

#### Problema
- Es privado.
- Escribe en el sistema de archivos.
- Usa `randomInt()` y `randomFloat()`.

#### Solución
Hacer el método package-private y usar `@TempDir` de JUnit 5.

**Modificar en GeneradorFichero.java:**
```java
static void generateFile(Path path, int nTransfers) throws IOException {
    try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))){
        for(int i = 0; i < nTransfers; i++){
            pw.println(String.format("%d;%.2f",
                    randomInt(MIN_ACCOUNT_NUMBER, MAX_ACCOUNT_NUMBER),
                    randomFloat(MIN_PAYROLL_AMOUNT, MAX_PAYROLL_AMOUNT)));
        }
    }
}
```

**Tests:**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GeneradorFicheroGenerateFileTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateFile_CreatesFileWithCorrectNumberOfLines() throws IOException {
        Path testFile = tempDir.resolve("test_output.txt");
        int expectedLines = 10;

        GeneradorFichero.generateFile(testFile, expectedLines);

        assertTrue(Files.exists(testFile));
        List<String> lines = Files.readAllLines(testFile);
        assertEquals(expectedLines, lines.size());
    }

    @Test
    void testGenerateFile_EachLineHasCorrectFormat() throws IOException {
        Path testFile = tempDir.resolve("test_format.txt");

        GeneradorFichero.generateFile(testFile, 5);

        List<String> lines = Files.readAllLines(testFile);
        for (String line : lines) {
            // Formato: numero;decimal (ej: 123456789;2500.50)
            assertTrue(line.matches("\\d+;\\d+\\.\\d{2}"),
                      "La línea no tiene el formato correcto: " + line);
        }
    }

    @Test
    void testGenerateFile_AccountNumbersInValidRange() throws IOException {
        Path testFile = tempDir.resolve("test_accounts.txt");

        GeneradorFichero.generateFile(testFile, 20);

        List<String> lines = Files.readAllLines(testFile);
        for (String line : lines) {
            String[] parts = line.split(";");
            int accountNumber = Integer.parseInt(parts[0]);

            assertTrue(accountNumber >= 100_000_000 && accountNumber <= 299_999_999,
                      "Número de cuenta fuera de rango: " + accountNumber);
        }
    }

    @Test
    void testGenerateFile_AmountsInValidRange() throws IOException {
        Path testFile = tempDir.resolve("test_amounts.txt");

        GeneradorFichero.generateFile(testFile, 20);

        List<String> lines = Files.readAllLines(testFile);
        for (String line : lines) {
            String[] parts = line.split(";");
            float amount = Float.parseFloat(parts[1]);

            assertTrue(amount >= 1500 && amount <= 3000,
                      "Cantidad fuera de rango: " + amount);
        }
    }

    @Test
    void testGenerateFile_ZeroTransfers() throws IOException {
        Path testFile = tempDir.resolve("test_zero.txt");

        GeneradorFichero.generateFile(testFile, 0);

        assertTrue(Files.exists(testFile));
        List<String> lines = Files.readAllLines(testFile);
        assertEquals(0, lines.size());
    }

    @Test
    void testGenerateFile_LargeNumberOfTransfers() throws IOException {
        Path testFile = tempDir.resolve("test_large.txt");
        int largeNumber = 1000;

        GeneradorFichero.generateFile(testFile, largeNumber);

        List<String> lines = Files.readAllLines(testFile);
        assertEquals(largeNumber, lines.size());
    }

    @Test
    void testGenerateFile_InvalidPath() {
        Path invalidPath = Path.of("/ruta/invalida/que/no/existe/archivo.txt");

        assertThrows(IOException.class, () -> {
            GeneradorFichero.generateFile(invalidPath, 5);
        });
    }

    @Test
    void testGenerateFile_FileContentIsNotEmpty() throws IOException {
        Path testFile = tempDir.resolve("test_content.txt");

        GeneradorFichero.generateFile(testFile, 3);

        long fileSize = Files.size(testFile);
        assertTrue(fileSize > 0, "El archivo está vacío");
    }
}
```

---

## Buenas prácticas

### 1. Organización de los tests

```
src/test/java/es/dm2e/psp/actividad01/grupo2/
├── GeneradorFicheroTest.java              # Tests generales
├── GeneradorFicheroIsGreaterTest.java     # Tests para isGreater()
├── GeneradorFicheroGenerateFileTest.java  # Tests para generateFile()
├── GeneradorFicheroInputTest.java         # Tests para getDataFromMain()
└── GeneradorFicheroMainTest.java          # Tests de integración del main()
```

### 2. Nombres descriptivos

Usa el patrón: `test{MethodName}_{Scenario}_{ExpectedResult}`

```java
@Test
void testGenerateFile_WithZeroTransfers_CreatesEmptyFile() { }

@Test
void testIsGreater_WhenValueEqualsThreshold_ReturnsTrue() { }
```

### 3. Patrón AAA (Arrange-Act-Assert)

```java
@Test
void testExample() {
    // Arrange: Preparar datos de prueba
    Path testFile = tempDir.resolve("test.txt");
    int transfers = 5;

    // Act: Ejecutar el método a probar
    GeneradorFichero.generateFile(testFile, transfers);

    // Assert: Verificar resultados
    assertTrue(Files.exists(testFile));
    assertEquals(transfers, Files.readAllLines(testFile).size());
}
```

### 4. Usar @BeforeEach y @AfterEach

```java
class GeneradorFicheroTest {

    @TempDir
    Path tempDir;

    private Path testFile;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test_file.txt");
    }

    @AfterEach
    void tearDown() {
        // Limpieza si es necesaria (aunque @TempDir se encarga)
    }

    @Test
    void testSomething() {
        // Usar testFile aquí
    }
}
```

### 5. Tests parametrizados

Para probar múltiples casos con la misma lógica:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ParameterizedTest
@CsvSource({
    "50, 30, true",
    "30, 30, true",
    "20, 30, false",
    "0, 0, true",
    "-5, -10, true"
})
void testIsGreater_MultipleScenarios(int value, int threshold, boolean expected) {
    assertEquals(expected, GeneradorFichero.isGreater(value, threshold));
}
```

### 6. Mocking de dependencias (si fuera necesario)

Aunque `GeneradorFichero` no usa muchas dependencias externas, aquí un ejemplo:

```java
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;

@Test
void testWithMocking() {
    try (MockedStatic<RandomGenerator> mockedRandom = mockStatic(RandomGenerator.class)) {
        mockedRandom.when(() -> RandomGenerator.randomInt(anyInt(), anyInt()))
                   .thenReturn(150_000_000);

        // Tu test aquí
    }
}
```

---

## Refactorización recomendada

Para hacer la clase más testeable, considera estos cambios:

### Cambio 1: Extraer la aleatoriedad del main

```java
public static void main(String[] args) {
    int randomFailure = randomInt(MIN_RANDOM_GENERATION_NUMBER, MAX_RANDOM_GENERATION_NUMBER);
    executeWithRandomCheck(randomFailure);
}

static void executeWithRandomCheck(int randomValue) {
    if (isGreater(randomValue, NUMBER_THRESHOLD)) {
        getDataFromMain();
    } else {
        System.out.println("Random failure!");
        System.exit(100);
    }
}
```

### Cambio 2: Inyectar dependencias

```java
static void processTransfers(BufferedReader reader, Path outputPath) throws IOException {
    String directoryName = reader.readLine();
    String fileName = reader.readLine();
    int transferencias = Integer.parseInt(reader.readLine());

    generateFile(outputPath, transferencias);
}
```

### Cambio 3: Separar lógica de I/O

```java
static List<String> generateTransferLines(int nTransfers) {
    List<String> lines = new ArrayList<>();
    for(int i = 0; i < nTransfers; i++){
        lines.add(String.format("%d;%.2f",
                randomInt(MIN_ACCOUNT_NUMBER, MAX_ACCOUNT_NUMBER),
                randomFloat(MIN_PAYROLL_AMOUNT, MAX_PAYROLL_AMOUNT)));
    }
    return lines;
}

static void writeLinesToFile(Path path, List<String> lines) throws IOException {
    Files.write(path, lines);
}
```

---

## Ejemplo completo de clase de test

```java
package es.dm2e.psp.actividad01.grupo2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para GeneradorFichero")
class GeneradorFicheroCompleteTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Debe crear archivo con el número correcto de líneas")
    void testFileGenerationWithCorrectLineCount() throws IOException {
        Path testFile = tempDir.resolve("transfers.txt");
        int expectedLines = 15;

        GeneradorFichero.generateFile(testFile, expectedLines);

        List<String> lines = Files.readAllLines(testFile);
        assertEquals(expectedLines, lines.size(),
            "El archivo debe contener exactamente " + expectedLines + " líneas");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 100, 1000})
    @DisplayName("Debe generar archivo con diferentes cantidades de transferencias")
    void testFileGenerationWithVariousTransferCounts(int count) throws IOException {
        Path testFile = tempDir.resolve("transfers_" + count + ".txt");

        GeneradorFichero.generateFile(testFile, count);

        assertEquals(count, Files.readAllLines(testFile).size());
    }

    @Test
    @DisplayName("isGreater debe retornar true cuando valor es mayor o igual")
    void testIsGreaterReturnsTrueWhenValueIsGreaterOrEqual() {
        assertTrue(GeneradorFichero.isGreater(30, 30));
        assertTrue(GeneradorFichero.isGreater(31, 30));
    }

    @Test
    @DisplayName("isGreater debe retornar false cuando valor es menor")
    void testIsGreaterReturnsFalseWhenValueIsLess() {
        assertFalse(GeneradorFichero.isGreater(29, 30));
    }
}
```

---

## Cobertura de código

Para medir la cobertura de tus tests, usa JaCoCo:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Ejecutar: `mvn clean test jacoco:report`

Ver reporte en: `target/site/jacoco/index.html`

---

## Comandos útiles

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar una clase específica de tests
mvn test -Dtest=GeneradorFicheroTest

# Ejecutar un test específico
mvn test -Dtest=GeneradorFicheroTest#testIsGreater_ValueGreaterThanThreshold

# Ejecutar tests con cobertura
mvn clean test jacoco:report

# Ver resultados detallados
mvn test -X
```

---

## Resumen de cambios necesarios en GeneradorFichero.java

Para hacer la clase totalmente testeable:

1. Cambiar `private static boolean isGreater` → `static boolean isGreater`
2. Cambiar `private static void generateFile` → `static void generateFile` y cambiar `System.exit()` por `throws IOException`
3. Extraer `getDataFromMain()` para aceptar un `InputStream` como parámetro
4. Considerar extraer la lógica de generación random en un método separado

Con estos cambios, podrás escribir tests completos y robustos para toda la funcionalidad.
