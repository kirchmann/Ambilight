from dataclasses import dataclass
import mss
from typing import Tuple
from resolution_handler import ScreenResolutionMonitor  # your threaded class
import threading

@dataclass
class Resolution:
    width: int
    height: int

class AmbilightCalculator:
    def __init__(self, leds_width: int, leds_height: int, sample_depth: int, serial_communication_callback):
        """
        :param leds_width: number of LEDs horizontally
        :param leds_height: number of LEDs vertically
        :param sample_depth: depth of pixels to sample for colors
        :param serial_communication_callback: function to send calculated colors
        """
        self.leds_width = leds_width
        self.leds_height = leds_height
        self.sample_depth = sample_depth
        self.serial_communication_callback = serial_communication_callback

        # Start the screen resolution monitor
        self.resolution_monitor = ScreenResolutionMonitor(callback=self._on_resolution_change)
        self.resolution_monitor.start()

    def _on_resolution_change(self, width: int, height: int):
        print(f"[Ambilight] Screen resolution changed: {width}x{height}")
        self.screen_resolution.width = width
        self.screen_resolution.height = height
        # do some math and then start the calculation thread

    @staticmethod
    def RGBToBytes(rgb: Tuple[int, int, int]) -> bytes:
        """Convert an RGB tuple to bytes."""
        r, g, b = rgb
        return bytes([r, g, b])  # Check order of colors matches your Arduino code. pre-amble will be handled in serial communication

    def stop(self):
        print("Stopping the resolution monitor thread.")
        self.resolution_monitor.stop()
        self.resolution_monitor.join()

if __name__ == "__main__":
    # Example usage of AmbilightCalculator
    def dummy_serial_callback(data: bytes):
        print(f"Sending data to serial: {data}")

    ambi_calc = AmbilightCalculator(
        leds_width=30,
        leds_height=15,
        sample_depth=10,
        serial_communication_callback=dummy_serial_callback
    )

    try:
        # Keep the main thread alive while monitoring
        while True:
            threading.Event().wait(1.0)
    except KeyboardInterrupt:
        print("Stopping AmbilightCalculator...")
        ambi_calc.stop()