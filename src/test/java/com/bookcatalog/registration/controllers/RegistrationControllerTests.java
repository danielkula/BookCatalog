package com.bookcatalog.registration.controllers;

import com.bookcatalog.registration.AccountActivationService;
import com.bookcatalog.registration.RegistrationMailSender;
import com.bookcatalog.registration.UserRegistrationService;
import com.bookcatalog.registration.model.User;
import com.bookcatalog.registration.model.UserDto;
import com.bookcatalog.registration.validation.UserDtoValidator;
import com.bookcatalog.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTests {
    @Mock
    private UserRegistrationService userRegistrationService;
    @Mock
    private UserDtoValidator userDtoValidator;
    @Mock
    private RegistrationMailSender registrationMailSender;
    @Mock
    private AccountActivationService accountActivationService;
    @InjectMocks
    private RegistrationController registrationController;
    @Mock
    private UserDto userDto;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private WebRequest webRequest;
    @Mock
    private User user;
    private String notRelevant = "";

    @Test(expected = ValidationException.class)
    public void whenValidationErrorsThenThrowsValidationException() {
        when(bindingResult.hasErrors()).thenReturn(true);

        registrationController.register(null, bindingResult, null);
    }

    @Test
    public void postRegisterRegistersNewAccount(){
        when(bindingResult.hasErrors()).thenReturn(false);

        registrationController.register(userDto, bindingResult, webRequest);

        verify(userRegistrationService, times(1)).registerNewUserAccount(userDto);
    }

    @Test
    public void postRegisterSendsActivationMail() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(webRequest.getContextPath()).thenReturn(notRelevant);
        when(userRegistrationService.registerNewUserAccount(userDto)).thenReturn(user);
        registrationController.register(userDto, bindingResult, webRequest);

        verify(registrationMailSender).sendRegistrationMail(eq(user), Matchers.any(String.class), eq(notRelevant));
    }

    @Test
    public void getRegisterActivatesAccount() {
        String token = "";

        registrationController.register(token);

        verify(accountActivationService).activateAccount(token);
    }
}
