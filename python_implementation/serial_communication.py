import serial
import time

PRE_AMBLE = bytes(range(10))

class SerialCommunication:
    def __init__(self, port: str, baudrate: int = 115200, timeout: float = 1.0):
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.connection = None

    def open(self, attempts: int = 20, delay: float = 0.5):
        for attempt in range(1, attempts + 1):
            try:
                self.connection = serial.Serial(self.port, self.baudrate, timeout=self.timeout)
                break  # success
            except serial.SerialException as e:
                print(f"[WARN] Cannot open {self.port} (attempt {attempt}/{attempts}): {e}")
                time.sleep(delay)
        else:
            raise ConnectionError(f"Failed to open {self.port} after {attempts} attempts.")

        # Clear any stale data
        self.connection.reset_input_buffer()
        self.connection.reset_output_buffer()
        print(f"[OK] Opened serial port {self.port} at {self.baudrate} baud")
        time.sleep(3.0)  # Arduino boot reset delay - guesswork at timing


    def close(self):
        if self.connection is not None:
            try:
                if self.connection.is_open:
                    # give Arduino a moment to finish any processing
                    try:
                        self.connection.flush()
                    except Exception:
                        pass
                    self.connection.close()
                    print(f"[OK] Closed serial port {self.port}")
            finally:
                self.connection = None

    def send_data(self, data: bytes):
        """
        Prepend PRE_AMBLE and send the full packet. Do a small flush and short sleep
        to let the Arduino process the bytes. Also throttle frames to avoid overlap.
        """
        if self.connection and self.connection.is_open:
            packet = PRE_AMBLE + data
            self.connection.write(packet)
            # debug
            #print(f"[TX] Sent ({len(packet)} bytes): {packet[:30].hex()} ...")
        else:
            raise ConnectionError("Serial connection is not open.")

    def read_data(self, size: int = 1) -> bytes:
        if self.connection and self.connection.is_open:
            incoming = self.connection.read(size)
            print(f"[RX] Received: {incoming}")
            return incoming
        else:
            raise ConnectionError("Serial connection is not open.")

    def __enter__(self):
        if not (self.connection and self.connection.is_open):
            self.open()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()
        return False

if __name__ == "__main__":
    # try the SerialCommunication class
    with SerialCommunication(port="COM3", baudrate=115200) as ser:
        ser.send_data(b'Hello, Arduino!')
        response = ser.read_data(16)
        print(f"Response from Arduino: {response}")
