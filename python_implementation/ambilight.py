from pathlib import Path
from settings_reader import load_settings
from serial_communication import SerialCommunication
from ambilight_calculator import AmbilightCalculator
import time
import argparse
import sys


class AmbilightController:
    def __init__(self, leds_per_side: int, leds_height: int, sample_depth: int, fps: int, baud_rate: int, serial_port: str):
        self.leds_per_side = leds_per_side
        self.leds_height = leds_height
        self.sample_depth = sample_depth
        self.fps = fps
        self.serial_port = serial_port
        self.baud_rate = baud_rate
        self.serialcom = SerialCommunication(port=self.serial_port, baudrate=self.baud_rate)
        self.ambilight_calculator = AmbilightCalculator(leds_per_side, leds_height, sample_depth, fps, self.serialcom.send_data)

    def __enter__(self):
        self.serialcom.open()
        self.ambilight_calculator.start()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.ambilight_calculator.stop()
        self.serialcom.close()
        return False


def get_settings_path():
    parser = argparse.ArgumentParser(description="Ambilight controller")
    parser.add_argument(
        "settings",
        nargs="?",
        type=Path,
        help="Full path to ambilight_settings.json"
    )

    args = parser.parse_args()

    # Default: settings next to script
    if args.settings:
        settings_path = args.settings
    else:
        settings_path = Path(__file__).parent / "ambilight_settings.json"

    if not settings_path.exists():
        print(f"ERROR: Settings file not found:\n{settings_path}")
        sys.exit(1)

    return settings_path


if __name__ == "__main__":
    path = get_settings_path()
    settings = load_settings(path)

    with AmbilightController(
        leds_per_side=settings.neopixels.width,
        leds_height=settings.neopixels.height,
        sample_depth=settings.sampling_depth,
        fps=settings.fps,
        serial_port=settings.comport,
        baud_rate=settings.baud_rate,
    ) as ambi:
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            print("Exiting on user request.")
