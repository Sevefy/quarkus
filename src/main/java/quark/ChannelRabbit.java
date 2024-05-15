package quark;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Setter
@Getter
@ApplicationScoped
public class ChannelRabbit {
    @Channel("first-queue")
    Emitter<String> emiter;

    private String newMessage;
    private String sendMessage;


    public void sendMessage() throws InterruptedException {
        this.emiter.send(this.sendMessage);
        Thread.sleep(250);
    }

    @Incoming("second-queue")
    public void modifiedMessage(String modified_message){
        setNewMessage(modified_message);
        Log.info(modified_message);
    }
}
