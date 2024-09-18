package edu.epam.fop.mocks;

import edu.epam.fop.mocks.client.ClientRepository;
import edu.epam.fop.mocks.client.ClientResponse;
import org.mockito.Mockito;

public final class ClientService {

  private final ClientRepository client;

    public ClientService(ClientRepository client) {
        this.client = client;
        long definedId = client.definedId();
        Mockito.when(client.findById(definedId)).thenThrow(UnsupportedOperationException.class);
    }

    public ClientResponse search(long id) {
        return client.findById(id);
    }
}
