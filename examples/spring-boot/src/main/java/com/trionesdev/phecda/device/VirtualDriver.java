package com.trionesdev.phecda.device;

import cn.hutool.core.collection.ListUtil;
import com.trionesdev.phecda.device.sdk.interfaces.DeviceServiceSDK;
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver;
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
    @Override
    public void initialize(@NotNull DeviceServiceSDK sdk) {

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
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), "Bool", true));
                    break;
                case VALUE_TYPE_UINT8:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_UINT8, 1));
                case VALUE_TYPE_UINT16:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_UINT16, 1));
                    break;
                case VALUE_TYPE_UINT32:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_UINT32, 1));
                    break;
                case VALUE_TYPE_UINT64:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_UINT64, 1));
                    break;
                case VALUE_TYPE_INT8:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_INT8, 1));
                    break;
                case VALUE_TYPE_INT16:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_INT16, 1));
                    break;
                case VALUE_TYPE_INT32:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_INT32, 1));
                    break;
                case VALUE_TYPE_INT64:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_INT64, 1));
                    break;
                case VALUE_TYPE_FLOAT32:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_FLOAT32, 1));
                    break;
                case VALUE_TYPE_FLOAT64:
                    cvs.add(CommandValue.newCommandValue(req.getDeviceResourceName(), VALUE_TYPE_FLOAT64, 1));
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
}
