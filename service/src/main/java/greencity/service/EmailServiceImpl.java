package greencity.service;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.subscriber.NewsSubscriberVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final Executor executor;
    private final String ecoNewsLink;
    private final String serverLink;
    private final String senderEmailAddress;

    @Autowired
    public EmailServiceImpl(
            JavaMailSender javaMailSender,
            ITemplateEngine templateEngine,
            @Qualifier("emailServiceExecutor") Executor executor,
            @Value("econews.address") String ecoNewsLink,
            @Value("address") String serverLink,
            @Value("sender.email.address") String senderEmailAddress
    ) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.ecoNewsLink = ecoNewsLink;
        this.serverLink = serverLink;
        this.senderEmailAddress = senderEmailAddress;
    }

    @Override
    public void sendNewNewsForSubscriber(List<NewsSubscriberVO> subscribers, AddEcoNewsDtoResponse newsDto) {
        Map<String, Object> model = new HashMap<>();
        model.put("ecoNewsLink", ecoNewsLink);
        model.put("news", newsDto);
        for (NewsSubscriberVO subscriber : subscribers) {
            model.put("unsubscribeLink", serverLink + "/newsSubscriber/unsubscribe?email="
                                         + URLEncoder.encode(subscriber.getEmail(), StandardCharsets.UTF_8)
                                         + "&unsubscribeToken=" + subscriber.getUnsubscribeToken());
            String template = createEmailTemplate(model, "news-receive-email-page");
            sendEmail(subscriber.getEmail(), "news", template);
        }
    }

    @Override
    public void sendConfirmationLetter(NewsSubscriberVO subscriber) {
        Map<String, Object> model = new HashMap<>();
        String subscriberEmail = subscriber.getEmail();
        model.put("email", subscriberEmail);
        model.put("ecoNewsLink", ecoNewsLink);
        model.put("unsubscribeLink", serverLink + "/newsSubscriber/unsubscribe?email="
                                     + URLEncoder.encode(subscriberEmail, StandardCharsets.UTF_8)
                                     + "&unsubscribeToken=" + subscriber.getUnsubscribeToken());
        String template = createEmailTemplate(model, "news-receive-confirmation-email-page");
        sendEmail(subscriber.getEmail(), "news receive confirmation", template);
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info("Creating email template {} with parameters: {}", templateName, vars);
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process("core/" + templateName, context);
    }

    private void sendEmail(String receiverEmail, String subject, String content) {
        log.info("Sending email to {} on subject: {}", receiverEmail, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(senderEmailAddress);
            mimeMessageHelper.setTo(receiverEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessage.setContent(content, "text/html; charset=utf-8");
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        executor.execute(() -> javaMailSender.send(mimeMessage));
    }
}
