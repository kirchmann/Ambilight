import mss
import numpy as np
from typing import Tuple, Dict
from PIL import Image

ROI = Tuple[int, int, int, int]  # left, top, width, height

class Screenshotter:
    def __init__(self, monitor_index: int = 1):
        """
        monitor_index: which mss monitor to use (1 = primary)
        """
        self.monitor_index = monitor_index
        self._sct = mss.mss()

    def _make_region(self, roi: ROI) -> Dict[str, int]:
        left, top, width, height = roi
        return {"left": int(left), "top": int(top), "width": int(width), "height": int(height)}

    def capture(self, roi: ROI) -> np.ndarray:
        """
        Capture and return an RGB numpy array shape (h, w, 3).
        roi is required: (left, top, width, height)
        """
        if roi is None:
            raise ValueError("roi must be provided to capture()")
        region = self._make_region(roi)
        img = self._sct.grab(region)
        arr = np.array(img)  # BGRA
        rgb = arr[..., :3][..., ::-1].astype(np.uint8)  # convert BGR(A) -> RGB
        return rgb


    def close(self):
        self._sct.close()


    # context manager support
    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()


if __name__ == "__main__":
    # quick test (must pass an ROI to capture)
    with Screenshotter() as s:
        roi = (0, 0, 40, 1440)  # example: left, top, width, height
        img = s.capture(roi)  # roi is required now
        print("Captured", img.shape, "section", img.shape)

        # Open the cropped section in the default image viewer
        Image.fromarray(img).show(title="Captured section")

        # Also save it to disk for inspection
        Image.fromarray(img).save("captured_section.png")
        print("Saved section to captured_section.png")