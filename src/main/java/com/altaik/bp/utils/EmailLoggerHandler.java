package com.altaik.bp.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class EmailLoggerHandler extends Handler {
    private Properties properties;
    private String host = "88.204.230.205";
    private String port = "25";
    private String login = "delivery@com.altaik.db.altatender.kz";
    private String password = "ghjc20vjnh";
    private String name = "EmailLoggerHandler";
    private String to = "logging1system@gmail.com";
    private Session session;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
        }

        return properties;
    }

    public Session getSession() {
        if (session == null) {
            session = Session.getInstance(getProperties(),
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(login, password);
                        }
                    });
        }

        return session;
    }

    private void sendMail(String subject, String body) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(getSession());
//        message.setHeader("Content-Type", "text/html; charset=UTF-8");
        message.setFrom(new InternetAddress(login, name));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        ((MimeMessage) message).setText(body, "utf8");

        Transport.send(message);
    }

    @Override
    public void publish(LogRecord record) {
        String message = String.format("%s:%s:%s:<%s>", record.getLevel(), record.getSourceClassName(), record.getSourceMethodName(), record.getMessage());

        try {
            sendMail(name + " " + record.getLevel(), message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
