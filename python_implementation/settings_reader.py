from dataclasses import dataclass
from pathlib import Path
import json
from typing import Any, Dict, Optional, Union

@dataclass
class Neopixels:
    width: int
    height: int


@dataclass
class Settings:
    neopixels: Neopixels
    fps: int
    comport: str
    baud_rate: int
    sampling_depth: int


def _load_json(path: Path) -> Dict[str, Any]:
    if not path.exists():
        raise FileNotFoundError(f"Settings file not found: {path}")
    with path.open("r", encoding="utf-8") as f:
        return json.load(f)


def _validate_int(value: Any, name: str) -> int:
    if not isinstance(value, int):
        raise ValueError(f"{name} must be an integer (got {type(value).__name__})")
    return value


def load_settings(path: Path) -> Settings:
    """
    Load and validate settings from ambilight_settings.json.

    Default file name: ambilight_settings.json in the same directory as this module.
    """
    
    print (f"Loading settings from: {path}")

    data = _load_json(path)

    # Validate top-level keys
    if "neopixels" not in data:
        raise ValueError("Missing 'neopixels' section in settings")
    neop = data["neopixels"]
    if not isinstance(neop, dict):
        raise ValueError("'neopixels' must be an object")

    width = _validate_int(neop.get("width"), "neopixels.width")
    height = _validate_int(neop.get("height"), "neopixels.height")
    fps = _validate_int(data.get("fps"), "fps")

    comport = data.get("comport")
    if not isinstance(comport, str) or not comport:
        raise ValueError("comport must be a non-empty string")

    sampling_depth = _validate_int(data.get("sampling_depth"), "sampling_depth")

    baud_rate = _validate_int(data.get("baud_rate"), "baud_rate")

    return Settings(
        neopixels=Neopixels(width=width, height=height),
        fps=fps,
        sampling_depth=sampling_depth,
        baud_rate=baud_rate,
        comport=comport,
    )


if __name__ == "__main__":
    # quick test / demonstration
    try:
        path = Path(__file__).parent / "ambilight_settings.json"
        settings = load_settings(path)  # looks for ambilight_settings.json next to this file
        print(settings)
    except Exception as e:
        print(f"Failed to load settings: {e}")