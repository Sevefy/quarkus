package quark;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ConsulClass {
    private static final Logger LOGGER = Logger.getLogger(ConsulClass.class);
    private String instanceId;

    Consul consulClient = Consul.builder().withHostAndPort(HostAndPort.fromParts("consul", 8500)).build();
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus")
    String appName;
    @ConfigProperty(name = "quarkus.application.version", defaultValue = "3")
    String appVersion;
    @ConfigProperty(name = "quarkus.http.port")
    int port;

    void onStart(@Observes StartupEvent ev) {
        ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            instanceId = appName + "-" + port;
            LOGGER.info("instanceId - " + instanceId);
            ImmutableRegistration registration = ImmutableRegistration.builder()
                    .id(instanceId)
                    .name(appName + port)
                    .address("127.0.0.1")
                    .port(port)
                    .putMeta("version", appVersion)
                    .build();
            consulClient.agentClient().register(registration);
            LOGGER.info("Instance registered: id={}, address=127.0.0.1:{" + registration.getId() + "}");
        }, 5000, TimeUnit.MILLISECONDS);
    }

    void onStop(@Observes ShutdownEvent ev) {
        consulClient.agentClient().deregister(instanceId);
        LOGGER.info("Instance de-registered: id={" + instanceId + "}");
    }
}