package edu.epam.fop.mocks;

import edu.epam.fop.mocks.client.ClientRepository;
import edu.epam.fop.mocks.client.ClientResponse;

import static org.mockito.Mockito.when;

public final class ClientService {

  private final ClientRepository client;

  public ClientService(ClientRepository client) {
    this.client = client;

    // Mocking the findById method
    long definedId = client.definedId();
    ClientResponse response = new ClientResponse(definedId, "Lou", "Tenat");

    // Setting up the mock behavior using thenReturn
    when(client.findById(definedId)).thenReturn(response);
    when(client.findById(definedId + 1)).thenReturn(null);
  }

  public ClientResponse search(long id) {
    return client.findById(id);
  }
}
