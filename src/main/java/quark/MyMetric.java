package quark;

import org.eclipse.microprofile.metrics.MetricRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
public class MyMetric {
    @Setter
    @Getter
    private int digit;


    @Inject
    MetricRegistry registry;

    public void setRandomDigitMetric(){
        registry.gauge("random_digit",()-> this.digit);
    }

}
