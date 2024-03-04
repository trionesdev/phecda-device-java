package com.trionesdev.phecda.device;

import com.trionesdev.phecda.device.sdk.interfaces.DeviceDriverServiceSDK;
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VirtualDriver implements ProtocolDriver {
    @Override
    public void initialize(@NotNull DeviceDriverServiceSDK sdk) {

    }

    @Override
    public void start() {

    }
}
