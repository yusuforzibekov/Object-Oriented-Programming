package edu.epam.fop.mocks;

import edu.epam.fop.mocks.client.ClientResponse;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

@SuppressWarnings("UnusedReturnValue")
public interface TestUtils {

  static String toSourceCodeReference(CtElement element) {
    try {
      SourcePosition pos = element.getPosition();
      return String.format("%s.java:%d",
          pos.getCompilationUnit().getMainType().getQualifiedName(),
          pos.getLine());
    }
    catch (Exception e) {
      return null;
    }
  }
  
  static ClientResponseAssert assertThat(ClientResponse response) {
    return new ClientResponseAssert(response);
  }
  
  static class ClientResponseAssert extends AbstractObjectAssert<ClientResponseAssert, ClientResponse> {

    public ClientResponseAssert(ClientResponse clientResponse) {
      super(clientResponse, ClientResponseAssert.class);
    }
    
    public ClientResponseAssert hasId(long id) {
      return isNotNull()
          .satisfies(resp -> Assertions.assertThat(resp.id()).isEqualTo(id));
    }
    
    public ClientResponseAssert hasName(String name) {
      return isNotNull().satisfies(resp -> Assertions.assertThat(resp.name()).isEqualTo(name));
    }
    
    public ClientResponseAssert hasSurname(String surname) {
      return isNotNull().satisfies(resp -> Assertions.assertThat(resp.surname()).isEqualTo(surname));
    }
  }
}
