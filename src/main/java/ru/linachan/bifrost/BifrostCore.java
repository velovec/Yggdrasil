package ru.linachan.bifrost;

import gnu.io.*;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.*;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class BifrostCore implements SerialPortEventListener {

    private YggdrasilCore core;
    private SerialPort peripheralPort;

    private OutputStream portWriter;
    private InputStream portReader;

    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    private boolean exitOnFailure;

    public BifrostCore(YggdrasilCore core) {
        this.core = core;

        this.core.logInfo("Initializing Bifrost Peripheral Bridge...");

        String peripheralPortName = this.core.getConfig("BifrostPort", "/dev/ttyACM0");
        this.exitOnFailure = Boolean.valueOf(this.core.getConfig("BifrostExitOnFailure", "false"));

        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        try {
            core.enableFakeOutput();
            CommPortIdentifier portIdentifier = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
            while (portEnum.hasMoreElements()) {
                CommPortIdentifier currentPortIdentifier = (CommPortIdentifier) portEnum.nextElement();
                if (currentPortIdentifier.getName().equals(peripheralPortName)) {
                    portIdentifier = currentPortIdentifier;
                }
            }

            if (portIdentifier != null) {
                peripheralPort = (SerialPort) portIdentifier.open(this.getClass().getName(), TIME_OUT);
                setupPeripheralPort();
                this.core.logInfo("BifrostCore: Port '" + peripheralPortName + "' is ready!");
            } else {
                this.core.logWarning("BifrostCore: Port '" + peripheralPortName + "' not found!");
            }
            core.disableFakeOutput();
        } catch (PortInUseException e) {
            this.core.logException(e);
        }
    }

    private void setupPeripheralPort() {
        try {
            peripheralPort.setSerialPortParams(
                DATA_RATE,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
            );

            portWriter = peripheralPort.getOutputStream();
            portReader = peripheralPort.getInputStream();

            peripheralPort.addEventListener(this);
            peripheralPort.notifyOnDataAvailable(true);
        } catch (IOException | TooManyListenersException | UnsupportedCommOperationException e) {
            this.core.logException(e);
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch(serialPortEvent.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    byte[] incomingData = new byte[portReader.available()];
                    int actuallyRead = portReader.read(incomingData);
                    this.core.logInfo("BifrostCore: Read " + actuallyRead + " bytes of data: " + new String(incomingData));
                } catch (IOException e) {
                    this.core.logException(e);
                }
                break;
            default:
                this.core.logInfo("BifrostCore: Unhandled event on SerialPort: " + serialPortEvent.getEventType());
        }
    }

    public void writeByteArray(byte[] dataArray) {
        if (peripheralPort != null) {
            try {
                this.portWriter.write(dataArray);
            } catch (IOException e) {
                this.core.logException(e);
            }
        }
    }

    public synchronized void shutdownBifrost() {
        if (peripheralPort != null) {
            peripheralPort.removeEventListener();
            peripheralPort.close();
        }
    }

    public boolean execute_tests() {
        return !exitOnFailure || peripheralPort != null;
    }
}
