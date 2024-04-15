import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.hibernate.metadata.CollectionMetadata;
@Setter
@Getter
@ApplicationScoped
public class ChannelRabbit {

    private String new_message;
    @Channel("first-queue")
    Emitter<String> emiter;
    @Inject
    JsonObject currentMessage;


    public void send_message() throws InterruptedException {
        this.emiter.send(this.currentMessage.values().toString());
        Thread.sleep(250);
    }

    @Incoming("second-queue")
    public void setModified_message(String modified_message){
        setNew_message(modified_message);
        Log.info(modified_message);
    }
}
