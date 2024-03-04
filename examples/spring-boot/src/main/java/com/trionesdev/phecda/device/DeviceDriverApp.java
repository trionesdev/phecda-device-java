package com.trionesdev.phecda.device;

import com.trionesdev.phecda.device.sdk.startup.DeviceDriver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication()
public class DeviceDriverApp implements CommandLineRunner {
    private final VirtualDriver virtualDriver;

    public static void main(String[] args) {
        SpringApplication.run(DeviceDriverApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        DeviceDriver.bootstrap(args, "device-virtual", "0.0.1", virtualDriver);
    }
}
