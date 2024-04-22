package org.example.projectcalculator.service.utility;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.example.projectcalculator.model.User;

public class ServiceTestHelper {

  private ServiceTestHelper() {
  }

  public static void setSecurityContext(final User user) {
    final var authentication = mock(Authentication.class);
    final var userDetails = mock(UserDetails.class);

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn(user.getLogin());

    final var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
  }
}
