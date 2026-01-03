# Ambilight

Ambilight for computer screen using an arduino and neopixels

## Python implementation
Only tested with Python version: 3.12

### Create an .exe:

python -m venv ambilight_env

./.ambilight_env/Scripts/Activate.ps1

python -m pip install -r <some_path>Ambilight\python_implementation\requirements.txt

cd python_implementation

pyside6-deploy ambilight.py --name ambilight

./ambilight.exe "ambilight_settings.json"

Where you have to set the settings according to your setup.


## Built With

* Java 9
* Arduino

## RXTX setup
http://rxtx.qbang.org/wiki/index.php/Download
http://rxtx.qbang.org/wiki/index.php/Using_RXTX_In_Eclipse

## Eclipse export to runnable JAR
File -> Export -> Java -> Runnable JAR file, choose Library handling: Package required libraries into generated JAR

## Authors

* Carl Christian Kirchmann


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
