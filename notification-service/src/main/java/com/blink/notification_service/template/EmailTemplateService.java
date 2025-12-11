package com.blink.notification_service.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.blink.notification_service.dto.OrderEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/* For thymeleaf email templates */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {
    private final TemplateEngine templateEngine;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("tr", "TR"));
    
    private static final NumberFormat CURRENCY_FORMATTER = 
            NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

    public String generateOrderCreatedEmail(OrderEvent event) {
        Context context = createBaseContext(event);
        context.setVariable("title", "Siparişiniz Alındı!");
        context.setVariable("message", "Siparişiniz başarıyla oluşturuldu.  En kısa sürede işleme alınacaktır.");
        return templateEngine.process("order-created", context);
    }

    public String generateOrderConfirmedEmail(OrderEvent event) {
        Context context = createBaseContext(event);
        context.setVariable("title", "Siparişiniz Onaylandı!");
        context.setVariable("message", "Ödemeniz alındı ve siparişiniz onaylandı. Hazırlanmaya başlandı.");
        return templateEngine. process("order-confirmed", context);
    }

    public String generateOrderShippedEmail(OrderEvent event) {
        Context context = createBaseContext(event);
        context.setVariable("title", "Siparişiniz Kargoya Verildi!");
        context.setVariable("message", "Siparişiniz kargoya verildi. Takip numaranız ile kargonuzu takip edebilirsiniz.");
        context.setVariable("trackingNumber", event.getTrackingNumber());
        
        if (event.getEstimatedDeliveryDate() != null) {
            context.setVariable("estimatedDelivery", 
                    event.getEstimatedDeliveryDate().format(DATE_FORMATTER));
        }
        
        return templateEngine.process("order-shipped", context);
    }

    public String generateOrderDeliveredEmail(OrderEvent event) {
        Context context = createBaseContext(event);
        context.setVariable("title", "Siparişiniz Teslim Edildi!");
        context.setVariable("message", "Siparişiniz başarıyla teslim edildi. Bizi tercih ettiğiniz için teşekkür ederiz!");
        return templateEngine.process("order-delivered", context);
    }

    public String generateOrderCancelledEmail(OrderEvent event) {
        Context context = createBaseContext(event);
        context.setVariable("title", "Siparişiniz İptal Edildi");
        context.setVariable("message", "Siparişiniz iptal edilmiştir.");
        context.setVariable("cancellationReason", event.getCancellationReason());
        return templateEngine.process("order-cancelled", context);
    }

    private Context createBaseContext(OrderEvent event) {
        Context context = new Context();
        context.setVariable("orderNumber", event.getOrderNumber());
        context.setVariable("userEmail", event.getUserEmail());
        context.setVariable("totalAmount", CURRENCY_FORMATTER.format(event.getTotalAmount()));
        context.setVariable("totalItems", event.getTotalItems());
        context.setVariable("items", event.getItems());
        context.setVariable("timestamp", event.getTimestamp());
        return context;
    }
}
