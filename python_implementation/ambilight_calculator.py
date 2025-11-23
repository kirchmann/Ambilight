from dataclasses import dataclass
import numpy as np
from typing import Tuple, List
from resolution_handler import ScreenResolutionMonitor  # your threaded class
import threading
import screenshot_capture
import time

@dataclass
class Resolution:
    width: int
    height: int

class AmbilightCalculator:
    def __init__(self, leds_width: int, leds_height: int, sample_depth: int, fps: int, serial_communication_callback):
        """
        :param leds_width: number of LEDs horizontally
        :param leds_height: number of LEDs vertically
        :param sample_depth: depth of pixels to sample for colors
        :param serial_communication_callback: function to send calculated colors
        """
        self.leds_width = leds_width
        self.leds_height = leds_height
        self.sample_depth = sample_depth
        self.fps = fps
        self.serial_communication_callback = serial_communication_callback

        # ROIs (left, top, width, height) — None until resolution is known
        self.left_roi = None
        self.right_roi = None
        self.top_roi = None

        # synchronization
        self._roi_lock = threading.Lock()
        self._resolution_ready = threading.Event()
        self._stop_event = threading.Event()

        # Start the screen resolution monitor
        self.resolution_monitor = ScreenResolutionMonitor(callback=self._on_resolution_change)
        self.resolution_monitor.start()

        # Start capture thread (it will wait until resolution is available)
        self._capture_thread = threading.Thread(target=self._capture_loop, daemon=True)
        self._capture_thread.start()

    def _on_resolution_change(self, width: int, height: int):
        print(f"{type(self).__name__} Screen resolution monitor: {width}x{height}")
        left = (0, 0, self.sample_depth, height)
        right = (width - self.sample_depth, 0, self.sample_depth, height)
        top = (0, 0, width, self.sample_depth)

        with self._roi_lock:
            self.left_roi = left
            self.right_roi = right
            self.top_roi = top
            # notify capture thread that ROIs are available / updated
            self._resolution_ready.set()

    def _capture_loop(self):
        # Wait until _on_resolution_change sets the ROIs
        self._resolution_ready.wait()
        with screenshot_capture.Screenshotter() as s:
            while not self._stop_event.is_set():
                with self._roi_lock:
                    left = self.left_roi
                    right = self.right_roi
                    top = self.top_roi
                try:
                    # capture each ROI at full resolution (or use downsample helper)
                    left_img = s.capture(left)
                    right_img = s.capture(right)
                    top_img = s.capture(top)

                    # produce per-LED colors by splitting each strip into segments
                    top_colors = self._colors_from_horizontal_strip(top_img, self.leds_width)
                    left_colors = self._colors_from_vertical_strip(left_img, self.leds_height)
                    right_colors = self._colors_from_vertical_strip(right_img, self.leds_height)

                    # Order the colors left -> top -> right
                    # left_colors is computed top->bottom, reverse it so left goes bottom->top
                    left_colors_reversed = list(reversed(left_colors))

                    frame_colors: List[Tuple[int,int,int]] = []
                    frame_colors.extend(left_colors_reversed)   # left
                    frame_colors.extend(top_colors)             # top (left->right)
                    frame_colors.extend(right_colors)           # right (top->bottom)
                    data = self.colors_to_bytes(frame_colors)
                    self.serial_communication_callback(data)

                except Exception as exc:
                    print(f"{type(self).__name__} capture error: {exc}")
                time.sleep(1.0 / max(1, self.fps))

    def _average_color(self, img: np.ndarray) -> Tuple[int, int, int]:
        """Compute the average RGB color of the given image array."""
        if img.size == 0:
            return (0, 0, 0)
        avg = img.mean(axis=(0, 1))
        return tuple(int(c) for c in avg[:3])  # return as (R, G, B)

    def _colors_from_vertical_strip(self, img: np.ndarray, segments: int) -> List[Tuple[int,int,int]]:
        """Split a vertical strip image into `segments` along height and return average RGB per segment."""
        if img.size == 0 or segments <= 0:
            return [(0,0,0)] * max(0, segments)
        h = img.shape[0]
        colors: List[Tuple[int,int,int]] = []
        for i in range(segments):
            start = int(i * h / segments)
            end = int((i + 1) * h / segments)
            if end <= start:
                end = start + 1
            seg = img[start:end, :, :3]  # keep first 3 channels
            avg = seg.mean(axis=(0,1), dtype=np.float64)
            r,g,b = [int(round(x)) for x in avg[:3]]
            colors.append((max(0,min(255,r)), max(0,min(255,g)), max(0,min(255,b))))
        return colors

    def _colors_from_horizontal_strip(self, img: np.ndarray, segments: int) -> List[Tuple[int,int,int]]:
        """Split a horizontal strip image into `segments` along width and return average RGB per segment."""
        if img.size == 0 or segments <= 0:
            return [(0,0,0)] * max(0, segments)
        w = img.shape[1]
        colors: List[Tuple[int,int,int]] = []
        for i in range(segments):
            start = int(i * w / segments)
            end = int((i + 1) * w / segments)
            if end <= start:
                end = start + 1
            seg = img[:, start:end, :3]
            avg = seg.mean(axis=(0,1), dtype=np.float64)
            r,g,b = [int(round(x)) for x in avg[:3]]
            colors.append((max(0,min(255,r)), max(0,min(255,g)), max(0,min(255,b))))
        return colors

    @staticmethod
    def RGBToBytes(rgb: Tuple[int, int, int]) -> bytes:
        """Convert an RGB tuple to bytes."""
        r, g, b = rgb
        return bytes([r, g, b])  # Check order of colors matches your Arduino code. pre-amble will be handled in serial communication

    def colors_to_bytes(self, colors: List[Tuple[int,int,int]]) -> bytes:
        """Convert a list of RGB tuples into bytes: B,G,R per LED."""
        out = bytearray()
        for (r, g, b) in colors:
            out.extend((b & 0xFF, g & 0xFF, r & 0xFF))
        return bytes(out)

    def stop(self):
        print(f"{type(self).__name__} Stopping the resolution monitor and capture thread.")
        self._stop_event.set()
        self.resolution_monitor.stop()
        self.resolution_monitor.join()
        # wake capture thread if waiting
        self._resolution_ready.set()
        if self._capture_thread:
            self._capture_thread.join(timeout=1.0)

    def start(self):
        """
        Ensure the resolution monitor and capture thread are running.
        """
        # clear any previous stop request
        self._stop_event.clear()

        # start or restart resolution monitor if not running
        if self.resolution_monitor is None:
            self.resolution_monitor = ScreenResolutionMonitor(callback=self._on_resolution_change)

        if not self.resolution_monitor.is_alive:
            self.resolution_monitor.start()

        # start capture thread if not running
        if not self._capture_thread.is_alive:
            self._capture_thread.start()

if __name__ == "__main__":
    # Example usage of AmbilightCalculator
    def dummy_serial_callback(data: bytes):
        print(f": Sending data to serial: {data}")

    ambi_calc = AmbilightCalculator(
        leds_width=30,
        leds_height=15,
        sample_depth=10,
        fps=30,
        serial_communication_callback=dummy_serial_callback
    )

    try:
        # Keep the main thread alive while monitoring
        while True:
            threading.Event().wait(1.0)
    except KeyboardInterrupt:
        print("Stopping AmbilightCalculator...")
        ambi_calc.stop()