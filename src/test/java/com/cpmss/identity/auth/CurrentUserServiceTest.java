package com.cpmss.identity.auth;

import com.cpmss.people.common.EmailAddress;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.platform.exception.CommonErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Verifies database-backed resolution of the current authenticated user.
 *
 * <p>The service reloads AppUser state for ownership checks so resource
 * authorization uses current account role, active status, and person linkage.
 */
@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolvesActiveUserByAuthenticatedEmail() {
        UUID userId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        EmailAddress email = EmailAddress.of("guard@example.com");
        AppUser appUser = AppUser.builder()
                .id(userId)
                .email(email)
                .systemRole(SystemRole.GATE_GUARD)
                .personId(personId)
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email.value(), null, List.of()));
        when(appUserRepository.findByEmailAndActiveTrue(email)).thenReturn(Optional.of(appUser));

        CurrentUser currentUser = new CurrentUserService(appUserRepository).currentUser();

        assertThat(currentUser.userId()).isEqualTo(userId);
        assertThat(currentUser.personId()).isEqualTo(personId);
        assertThat(currentUser.systemRole()).isEqualTo(SystemRole.GATE_GUARD);
        assertThat(currentUser.email()).isEqualTo(email.value());
    }

    @Test
    void rejectsMissingAuthentication() {
        CurrentUserService service = new CurrentUserService(appUserRepository);

        assertThatThrownBy(service::currentUser)
                .isInstanceOf(ApiException.class)
                .satisfies(error -> assertThat(((ApiException) error).getErrorCode())
                        .isEqualTo(CommonErrorCode.SECURITY_CONTEXT_MISSING));
    }
}
