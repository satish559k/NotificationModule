package com.NotificationModule.NotificationModule.service.kafka;

import com.NotificationModule.NotificationModule.Repository.ProductRepo;
import com.NotificationModule.NotificationModule.Repository.UserRepo;
import com.NotificationModule.NotificationModule.dto.NotificationKafkaMessage;
import com.NotificationModule.NotificationModule.entity.Product;
import com.NotificationModule.NotificationModule.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@AllArgsConstructor
public class SendNotification {
    private final JavaMailSender mailSender;

    private final BlockingQueue<NotificationKafkaMessage> orderQueue = new LinkedBlockingQueue<>();
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    @Async("workerExecutor")
    public void sendEmailCampaign() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        while(true){
            try {
                if(!orderQueue.isEmpty()){
                    for (NotificationKafkaMessage order : orderQueue) {
                        System.out.println("[Worker Thread] Processing this message "+order.toString());
                        Optional<User> userData= userRepo.findById(order.getUserId());
                        Product product = productRepo.findByproductId(order.getProductId());
                        helper.setTo(userData.get().getEmail());
                        helper.setSubject("Received your Order");
                        helper.setText("Received your order for "+product.getProductName() +" of quantity "+order.getQuantity()+"\n Thanks For Shopping!", true);
                        mailSender.send(message);
                        log.info("email Send Successfully to"+userData.get().getEmail());
                        orderQueue.remove(order);
                    }
                }else{
                    System.out.println("[Worker Thread] No Message");
                    Thread.sleep(1000);
                }
            }catch (MessagingException e){
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @KafkaListener(topics = {"${notification.topicname}"},groupId = "${notification.group}")
    public void getNotificationDataConsumer(HashMap<String,Object> map, Acknowledgment ack) throws MessagingException {
        try{

            UUID productId = UUID.fromString(map.get("productId").toString());
            UUID userId = UUID.fromString(map.get("userId").toString());
            int quantity = (Integer)map.get("quantityOrder");

            NotificationKafkaMessage notificationKafkaMessage = new NotificationKafkaMessage(productId,userId,quantity);
            orderQueue.put(notificationKafkaMessage);
            System.out.println("[Consumer] Data we got:"+notificationKafkaMessage.toString());
            ack.acknowledge();//commit message

        } catch (Exception e) {
            System.out.println("[Consumer] getNotificationDataConsumer Error");
            log.error(e.getMessage());
        }
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void init() throws MessagingException {
        sendEmailCampaign();
    }
}
