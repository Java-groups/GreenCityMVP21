package greencity.service;

import greencity.constant.DateParserPatterns;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.subscriber.NewsSubscriberVO;
import greencity.dto.user.UserVO;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
            @Value("${unsubscribe.redirect.address}") String ecoNewsLink,
            @Value("${unsubscribe.address}") String serverLink,
            @Value("${sender.email.address}") String senderEmailAddress
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

    @Override
    public void sendEventCommentNotification(UserVO eventOwner, EventCommentVO comment) {
        Map<String, Object> model = new HashMap<>();
        model.put("eventOwner", eventOwner.getName());
        model.put("eventName", comment.getEvent().getTitle());
        model.put("commentatorName", comment.getUser().getName());
        Locale locale;
        try {
            locale = Locale.of(eventOwner.getLanguageVO().getCode());
        } catch (Exception e) {
            locale = Locale.ENGLISH;
        }
        model.put("commentAdditionDate", comment.getCreatedDate().format(
                DateTimeFormatter.ofPattern(DateParserPatterns.EVENT_COMMENT_EMAIL_NOTIFICATION_PATTERN, locale)));
        model.put("commentText", comment.getComment());
        model.put("eventLink", serverLink + "/event/{eventId}");
        String template = createEmailTemplate(model, "event-comment-notification-page");
        sendEmail(eventOwner.getEmail(), "event", template);
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
