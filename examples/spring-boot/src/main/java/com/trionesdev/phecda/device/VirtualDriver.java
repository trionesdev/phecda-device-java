package com.trionesdev.phecda.device;

import cn.hutool.core.collection.ListUtil;
import com.trionesdev.phecda.device.sdk.interfaces.DeviceServiceSDK;
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver;
import com.trionesdev.phecda.device.sdk.messaging.MessagingClient;
import com.trionesdev.phecda.device.sdk.model.CommandRequest;
import com.trionesdev.phecda.device.sdk.model.CommandValue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.trionesdev.phecda.device.contracts.common.CommonConstants.*;

@Slf4j
@Component
public class VirtualDriver implements ProtocolDriver {
    private DeviceServiceSDK sdk;
    private MessagingClient messagingClient;

    @Override
    public void initialize(@NotNull DeviceServiceSDK sdk) {
        this.sdk = sdk;
        messagingClient = sdk.messagingClient();
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
        List<CommandValue> cvs = ListUtil.toList();
        for (CommandRequest req : reqs) {
            switch (Objects.requireNonNull(req.getType())) {
                case VALUE_TYPE_BOOL:
                    cvs.add(CommandValue.newCommandValue(req.getIdentifier(), VALUE_TYPE_BOOL, true));
                    break;
                case VALUE_TYPE_INT:
                    cvs.add(CommandValue.newCommandValue(req.getIdentifier(), VALUE_TYPE_INT, 1));
                    break;
                case VALUE_TYPE_LONG:
                    cvs.add(CommandValue.newCommandValue(req.getIdentifier(), VALUE_TYPE_LONG, 1));
                    break;
                case VALUE_TYPE_FLOAT:
                    cvs.add(CommandValue.newCommandValue(req.getIdentifier(), VALUE_TYPE_FLOAT, 1));
                    break;
                case VALUE_TYPE_DOUBLE:
                    cvs.add(CommandValue.newCommandValue(req.getIdentifier(), VALUE_TYPE_DOUBLE, 1));
                    break;

            }
        }
        return cvs;
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

    @Override
    public void messagingConnectionLost(Throwable cause) {

    }
}
