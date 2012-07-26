/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GRBLCalc;

    import gnu.io.*;
    import java.io.*;
    import java.util.logging.Level;
    import java.util.logging.Logger;

/**
 *
 * @author wwinder
 */
public class SerialCommunicator implements SerialPortEventListener{
    
    // General variables
    private CommPort commPort;
    private InputStream in;
    private OutputStream out;
    public boolean isReady = true;
    
    synchronized boolean openCommPort(String name, int baud) throws Exception {

        boolean returnCode;

        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(name);
           
        if (portIdentifier.isCurrentlyOwned()) {
            returnCode = false;
        } else {
                this.commPort = portIdentifier.open(this.getClass().getName(), 2000);

                SerialPort serialPort = (SerialPort) this.commPort;
                serialPort.setSerialPortParams(baud,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                this.in = serialPort.getInputStream();
                this.out = serialPort.getOutputStream();

                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);  
                serialPort.notifyOnBreakInterrupt(true);
                
                returnCode = true;
        }

        return returnCode;
    }
        
    void closeCommPort() {
        try {
            in.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        SerialPort serialPort = (SerialPort) this.commPort;
        serialPort.removeEventListener();
        this.commPort.close();
    }
    
    /**
     * Sends a command to the serial device.
     * @param command   Command to be sent to serial device.
     */
    void sendStringToComm(String command) {        
        // Send command to the serial port.
        PrintStream printStream = new PrintStream(this.out);
        printStream.print(command);
        printStream.close();    
        isReady = false;
    }
    
    /**
     * Immediately sends a byte, used for real-time commands.
     */
    void sendByteImmediately(byte b) throws IOException {
        out.write(b);
    }
     
    
    @Override
    // Reads data as it is returned by the serial port.
    public void serialEvent(SerialPortEvent arg0) {

        if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            isReady = true;
        }
    }

}
