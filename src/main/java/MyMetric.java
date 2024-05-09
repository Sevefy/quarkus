import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class MyMetric {
    @Inject
    MeterRegistry registry;
    public void setRandomDigit(@Observes int randomDigit){
        registry.gauge("random_digit", randomDigit);
    }
}
