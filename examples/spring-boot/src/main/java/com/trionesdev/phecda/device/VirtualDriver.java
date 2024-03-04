package com.trionesdev.phecda.device;

import com.trionesdev.phecda.device.sdk.interfaces.DeviceDriverServiceSDK;
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver;
import com.trionesdev.phecda.device.sdk.model.CommandRequest;
import com.trionesdev.phecda.device.sdk.model.CommandValue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VirtualDriver implements ProtocolDriver {
    @Override
    public void initialize(@NotNull DeviceDriverServiceSDK sdk) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop(boolean force) {

    }

    @NotNull
    @Override
    public List<CommandValue> handleReadCommands(@NotNull String deviceName, @NotNull Map<String, Map<String, Object>> protocols, @NotNull List<CommandRequest> reqs) {
        return null;
    }

    @Override
    public void handleWriteCommands(@NotNull String deviceName, @NotNull Map<String, Map<String, Object>> protocols, @NotNull List<CommandRequest> reqs, @NotNull List<CommandValue> params) {

    }

    @Override
    public void addDevice(@NotNull String deviceName, @NotNull Map<String, Map<String, Object>> protocols, @NotNull String adminState) {

    }

    @Override
    public void updateDevice(@NotNull String deviceName, @NotNull Map<String, Map<String, Object>> protocols, @NotNull String adminState) {

    }

    @Override
    public void removeDevice(@NotNull String deviceName, @NotNull Map<String, Map<String, Object>> protocols) {

    }
}
