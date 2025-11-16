import serial

PRE_AMBLE = bytes(range(10))

class SerialCommunication:
    def __init__(self, port: str, baudrate: int = 115200, timeout: float = 1.0):
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.connection = None

    def open(self):
        self.connection = serial.Serial(self.port, self.baudrate, timeout=self.timeout)
        print(f"[OK] Opened serial port {self.port} at {self.baudrate} baud")

    def close(self):
        if self.connection is not None:
            try:
                if self.connection.is_open:
                    self.connection.close()
                    print(f"[OK] Closed serial port {self.port}")
            finally:
                self.connection = None

    def send_data(self, data: bytes):
        if self.connection and self.connection.is_open:
            self.connection.write(data)
            print(f"[TX] Sent: {data}")
        else:
            raise ConnectionError("Serial connection is not open.")

    def read_data(self, size: int = 1) -> bytes:
        if self.connection and self.connection.is_open:
            incoming = self.connection.read(size)
            return incoming
        else:
            raise ConnectionError("Serial connection is not open.")

    # Context manager support: opens on enter (if needed) and closes on exit
    def __enter__(self):
        if not (self.connection and getattr(self.connection, "is_open", False)):
            self.open()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        print("[INFO] Exiting SerialCommunication context manager")
        self.close()
        # Do not suppress exceptions
        return False

if __name__ == "__main__":
    # try the SerialCommunication class
    with SerialCommunication(port="COM3", baudrate=115200) as ser:
        ser.send_data(b'Hello, Arduino!')
        response = ser.read_data(16)
        print(f"Response from Arduino: {response}")
