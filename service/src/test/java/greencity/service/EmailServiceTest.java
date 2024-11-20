package greencity.service;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.subscriber.NewsSubscriberVO;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

import java.util.List;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    EmailService emailService;
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private ITemplateEngine templateEngine;

    @BeforeEach
    public void setup() {
        initMocks(this);
        emailService = new EmailServiceImpl(javaMailSender, templateEngine, Executors.newCachedThreadPool(),
                "http://localhost:4200", "http://localhost:4200",
                "test@email.com");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void sendNewNewsForSubscriberTest() {
        List<NewsSubscriberVO> subscriberVOList = List.of(ModelUtils.getNewsSubscriberVO());
        AddEcoNewsDtoResponse dtoResponse = ModelUtils.getAddEcoNewsDtoResponse();
        emailService.sendNewNewsForSubscriber(subscriberVOList, dtoResponse);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    void sendConfirmationLetterTest() {
        NewsSubscriberVO subscriberVO = ModelUtils.getNewsSubscriberVO();
        emailService.sendConfirmationLetter(subscriberVO);
        verify(javaMailSender).createMimeMessage();
    }
}
