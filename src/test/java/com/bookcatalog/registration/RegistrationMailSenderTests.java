package com.bookcatalog.registration;

import com.bookcatalog.registration.model.User;
import com.bookcatalog.registration.model.VerificationToken;
import com.bookcatalog.registration.repositories.VerificationTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationMailSenderTests {
    private RegistrationMailSender registrationMailSender;
    @Mock
    private VerificationTokenMaker verificationTokenMaker;
    @Mock
    private MailService mailService;
    @Mock
    private RegistrationMailGenerator registrationMailGenerator;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private VerificationToken expectedToken;
    @Mock
    private User user;
    private String mailDestination = "";
    private String host = "";
    private String contextName = "";
    private String token = "";
    @Mock
    private SimpleMailMessage mailMessage;

    @Before
    public void setUp(){
        registrationMailSender = new RegistrationMailSender(verificationTokenRepository, verificationTokenMaker, mailService, registrationMailGenerator);
    }

    @Test
    public void sendRegistrationMail_createAndSaveVerificationTokenToDatabase() {
        when(verificationTokenMaker.createVerificationToken(user)).thenReturn(expectedToken);

        registrationMailSender.sendRegistrationMail(user, null, null);

        verify(verificationTokenRepository, times(1)).save(expectedToken);
    }

    @Test
    public void sendRegistrationMail_sendEmailToUser() {
        when(verificationTokenMaker.createVerificationToken(user)).thenReturn(expectedToken);
        when(user.getEmail()).thenReturn(mailDestination);
        when(expectedToken.getToken()).thenReturn(token);
        when(registrationMailGenerator.generateMail(mailDestination, host, contextName, token)).thenReturn(mailMessage);

        registrationMailSender.sendRegistrationMail(user, host, contextName);

        verify(mailService, times(1)).send(mailMessage);
    }
}
