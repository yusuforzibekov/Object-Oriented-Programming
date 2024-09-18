package edu.epam.fop.mocks.client;

public interface ClientRepository {
  
  long definedId();
  
  ClientResponse findById(long id);
}
