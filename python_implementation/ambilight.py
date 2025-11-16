import numpy
import time
import mss

from settings_reader import load_settings

class AmbilightController:
    def __init__(
        self,
        leds_per_side: int,
        sample_depth: int,
        fps: int,
        baud_rate: int,
        serial_port: str,
    ):
        self.leds_per_side = leds_per_side
        self.sample_depth = sample_depth
        self.fps = fps
        self.serial_port = serial_port
        self.baud_rate = baud_rate

if __name__ == "__main__":
    # use the settings_reader to load settings
    settings = load_settings()
    print(settings)
    ambi = AmbilightController(
        leds_per_side=settings.neopixels.width,
        sample_depth=settings.sampling_depth,
        fps=settings.fps,
        serial_port=settings.comport,
        baud_rate=settings.baud_rate,
    )