import sys
import ctypes
import threading
import time

class ScreenResolutionMonitor(threading.Thread):
    def __init__(self, callback, poll_interval=1.0, name="ScreenResolutionMonitor"):
        """
        Monitors the primary screen resolution and calls `callback(width, height)` 
        whenever it changes.
        
        :param callback: function to call when resolution changes
        :param poll_interval: how often to check resolution (in seconds)
        :param name: thread name
        """
        super().__init__(daemon=True, name=name)
        if sys.platform != "win32":
            raise RuntimeError("ScreenResolutionMonitor only works on Windows.")
        
        self.callback = callback
        self.poll_interval = poll_interval
        self.user32 = ctypes.windll.user32
        self._stop_event = threading.Event()
        
        # Initialize with the current resolution
        self.last_width = self.user32.GetSystemMetrics(0)
        self.last_height = self.user32.GetSystemMetrics(1)
        self.callback(self.last_width, self.last_height)

    def run(self):
        while not self._stop_event.is_set():
            width = self.user32.GetSystemMetrics(0)
            height = self.user32.GetSystemMetrics(1)
            if width != self.last_width or height != self.last_height:
                self.last_width, self.last_height = width, height
                self.callback(width, height)
            time.sleep(self.poll_interval)
    
    def stop(self):
        self._stop_event.set()


# Example usage
def resolution_changed(width, height):
    print(f"[{threading.current_thread().name}] Screen resolution changed: {width} x {height}")

if __name__ == "__main__":
    monitor = ScreenResolutionMonitor(resolution_changed, poll_interval=1.0)
    monitor.start()
    try:
        while True:
            time.sleep(0.5)
    except KeyboardInterrupt:
        print("Stopping monitor...")
        monitor.stop()
        monitor.join()
