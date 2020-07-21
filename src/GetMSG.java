import com.rabbitmq.client.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.io.Reader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;

public class GetMSG {


    public static void main(String[] args) {

        String server_name = "";
        String account_email = "" ;
        String password_email = "";
        String server_email = "";
        String port = "";
        String rmq_srv1 = "";
        String rmq_srv2 = "";
        String rmq_user = "";
        String rmq_password = "";

        try {
            InetAddress myHost = InetAddress.getLocalHost();
            System.out.println(myHost.getHostName());
            server_name = "<br>" + myHost.getHostName();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        System.out.println("Hello World! BW");

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("/opt/taskapp/sendmail_config.json")) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);
            account_email = (String) jsonObject.get("account_email");
            password_email = (String) jsonObject.get("password_email");
            server_email = (String) jsonObject.get("server_email");
            port = (String) jsonObject.get("port");
            rmq_srv1 = (String) jsonObject.get("rmq_srv1");
            rmq_srv2 = (String) jsonObject.get("rmq_srv2");
            rmq_user = (String) jsonObject.get("rmq_user");
            rmq_password = (String) jsonObject.get("rmq_password");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Inner account_email = " + account_email);
        Process_GetMessage_BW(server_name,account_email,password_email,server_email,port,rmq_srv1,rmq_user,rmq_password);
        Process_GetMessage_BW_TEST(server_name,account_email,password_email,server_email,port,rmq_srv1,rmq_user,rmq_password);
/*
        Process_GetMessage_BW(server_name,account_email,password_email,server_email,port);
        Process_GetMessage_BW_TEST(server_name,account_email,password_email,server_email,port);
*/

    }

    private static void Process_GetMessage_BW(String server_name, String account_email, String password_email, String server_email, String port, String rmq_srv1, String rmq_user, String rmq_password) {
        try {
            String QUEUE_NAME = "BW-PART-QUEUE";
            String email_address = "info@northeasternstarch.com";
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rmq_srv1);
            factory.setUsername(rmq_user);
            factory.setPassword(rmq_password);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    String img = "<img src='http://www.cgc-carbon.com/images/BW_LOGO.PNG'><br>";
                    System.out.println(" [x] Received '" + message + "'");
                    Send_Email("BW", email_address, img, message + server_name , account_email ,password_email ,server_email,port);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception ex) {
            System.out.println("ERROR");
        }
    }

    private static void Process_GetMessage_BW_TEST(String server_name, String account_email, String password_email, String server_email, String port, String rmq_srv1, String rmq_user, String rmq_password) {
        try {
            String QUEUE_NAME = "BW_TEST-PART-QUEUE";
            String email_address = "info@northeasternstarch.com";
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rmq_srv1);
            factory.setUsername(rmq_user);
            factory.setPassword(rmq_password);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    String img = "<img src='http://www.cgc-carbon.com/images/BW_LOGO.PNG'><br>";
                    System.out.println(" [x] Received '" + message + "'");
                    Send_Email("BW", email_address, img, message + server_name , account_email ,password_email ,server_email,port);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception ex) {
            System.out.println("ERROR");
        }
    }

    private static void Send_Email(String Company, String email_address, String img, String message ,String account_email,String password_email,String server_email,String port) {
        try {

            String[] split_str = message.split("฿");
            String toEmail = split_str[0].replaceAll(";", ",");
            String subject = Company + " : " + split_str[1];
            String message_send = img + split_str[2];

            System.out.println("-------------------------");

            System.out.println("Filnal = " + account_email);
            System.out.println("Filnal = " + password_email);
            System.out.println("Filnal = " + server_email);
            System.out.println("Filnal = " + port);

            System.out.println("TLSEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", server_email); //SMTP Host
            props.put("mail.smtp.port", port); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account_email, password_email);
                }
            };

            System.out.println("auth = " + auth);

            Session session = Session.getInstance(props, auth);

            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(email_address, email_address));

            msg.setReplyTo(InternetAddress.parse("no_reply@cgc-carbon.com", false));

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            msg.setSubject(subject, "UTF-8");

            msg.setContent(message_send, "text/html; charset=utf-8");

            System.out.println("Message is ready");

            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.out.println(e);
        }
    }

}
