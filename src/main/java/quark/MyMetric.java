package quark;

import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@ApplicationScoped
public class MyMetric {

    @Inject
    MetricRegistry registry;

    public Response setRandomDigitMetric(){
        registry.gauge("random_digit",()-> new Random().nextInt(10));
        return Response.ok().build();
    }

}
