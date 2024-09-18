package edu.epam.fop.mocks;

import static edu.epam.fop.mocks.TestUtils.assertThat;

import edu.epam.fop.mocks.client.ClientRepository;
import edu.epam.fop.mocks.client.ClientResponse;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;

class ClientServiceTest {

  private static final Random RND = new Random();
  private static final long DEFINED_ODD_ID = 2 * RND.nextLong() + 1;
  private static final long DEFINED_EVEN_ID = 2 * RND.nextLong();

  private static final MockSettings MOCKITO_CONFIG = Mockito.withSettings()
      .verboseLogging();
  
  private ClientRepository client;
  
  @BeforeEach
  void setUp() {
    // arrange
    client = Mockito.mock(ClientRepository.class, MOCKITO_CONFIG);
  }

  @Test
  void shouldBeConstructed() {
    // action-assert
    Assertions.assertDoesNotThrow(() -> new ClientService(client));
    Mockito.when(client.definedId()).thenReturn(DEFINED_ODD_ID);
  }

  @Test
  void shouldReturnProperlyConfiguredOddResponse() {
    // arrange
    Mockito.when(client.definedId()).thenReturn(DEFINED_ODD_ID);
    ClientService service = new ClientService(client);

    // action
    ClientResponse response = service.search(DEFINED_ODD_ID);

    // assert
    assertThat(response)
        .isNotNull()
        .hasId(DEFINED_ODD_ID)
        .hasName("Lou")
        .hasSurname("Tenat");
  }

  @Test
  void shouldReturnProperlyConfiguredEvenResponse() {
    // arrange
    Mockito.when(client.definedId()).thenReturn(DEFINED_EVEN_ID);
    ClientService service = new ClientService(client);

    // action
    ClientResponse response = service.search(DEFINED_EVEN_ID);

    // assert
    assertThat(response)
            .isNotNull()
            .hasId(DEFINED_EVEN_ID)
            .hasName("Louisa")
            .hasSurname("Rodriguez");
  }

  @Test
  void shouldReturnNullForNonConfiguredResponse() {
    // arrange
    ClientService service = new ClientService(client);
    long id;
    do {
      id = RND.nextLong();
    } while (id == DEFINED_EVEN_ID);

    // action
    ClientResponse response = service.search(id);

    // assert
    assertThat(response).isNull();
  }
}