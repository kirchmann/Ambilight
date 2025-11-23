import serial
import time

PRE_AMBLE = bytes(range(10))

class SerialCommunication:
    def __init__(self, port: str, baudrate: int = 115200, timeout: float = 1.0):
        self.port = port
        self.baudrate = baudrate
        self.timeout = timeout
        self.connection = None
        self._last_send_time = 0.0
        self._min_frame_interval = 0.03  # seconds - throttle sends to avoid Arduino confusion

    def open(self):
        # open without toggling DTR/RTS that can reset Arduino
        # create port then explicitly clear DTR/RTS
        self.connection = serial.Serial(self.port, self.baudrate, timeout=self.timeout)
        try:
            # prevent auto-reset on some boards
            try:
                self.connection.setDTR(False)
            except Exception:
                pass
            try:
                self.connection.setRTS(False)
            except Exception:
                pass
            # clear any leftover input so Arduino doesn't see old bytes
            self.connection.reset_input_buffer()
            self.connection.reset_output_buffer()
        except Exception:
            pass
        print(f"[OK] Opened serial port {self.port} at {self.baudrate} baud")
        time.sleep(3.0)  # wait for Arduino to initialize if needed

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

            # throttle sends so Arduino has time to switch from processing to waiting for preamble
            now = time.time()
            since = now - self._last_send_time
            if since < self._min_frame_interval:
                time.sleep(self._min_frame_interval - since)

            # clear any stale input that could confuse Arduino's preamble matcher
            try:
                self.connection.reset_input_buffer()
            except Exception:
                pass
            # write & flush
            self.connection.write(packet)
            try:
                self.connection.flush()
            except Exception:
                pass
            # increase this slightly if you still see flicker
            time.sleep(0.02)
            self._last_send_time = time.time()
            # debug
            print(f"[TX] Sent ({len(packet)} bytes): {packet[:30].hex()} ...")
        else:
            raise ConnectionError("Serial connection is not open.")

    def read_data(self, size: int = 1) -> bytes:
        if self.connection and not self.connection.is_open:
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
