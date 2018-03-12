/*
  Serial Event example

 When new serial data arrives, this sketch adds it to a String.
 When a newline is received, the loop prints the string and
 clears it.

 A good test for this is to try it with a GPS receiver
 that sends out NMEA 0183 sentences.

 Created 9 May 2011
 by Tom Igoe

 This example code is in the public domain.

 http://www.arduino.cc/en/Tutorial/SerialEvent

 */
#include <FastLED.h>
//Constants
#define DATA_PIN 9;    // data pin to neopixels
const int NUM_LEDS = 30; // Number of leds

CRGB leds[NUM_LEDS];

byte MESSAGE_START[] = { 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02};
uint8_t MESSAG_START_LENGTH = 10;
uint8_t current_message_start_position = 0;


// Color takes RGB values, from 0,0,0 up to 255,255,255
// e.g. White = (255,255,255), Red = (255,0,0);
int red = 255;    //Value from 0(led-off) to 255(). 
int green = 0; 
int blue = 0;
const int ledPin = 13;       // the pin that the LED is attached to
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

int red_side[2][16];
char red_top[30];
int green_side[2][16];
int green_top[30];
int blue_side[2][16];
int blue_top[30];


void setup() {
  // initialize serial:
  Serial.begin(115200);
  Serial.setTimeout(50);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
    // initialize the LED as an output:
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin,LOW);
  FastLED.addLeds<WS2812B, 9, GRB>(leds, NUM_LEDS);
  //FastLED.addLeds<NEOPIXEL, DATA_PIN>(leds, NUM_LEDS);

}

void loop() {
  // print the string when a newline arrives:
  int i = 0;
  if (Serial.available() > 0) {
    Serial.readBytesUntil('A', red_top, 30);

   /* if (text==255) {
      digitalWrite(ledPin,HIGH);
      delay(2);
    }*/
  }
          for(int dot = 0; dot < NUM_LEDS; dot++) { 
            leds[dot].blue = 255;
            FastLED.show();
            // clear this led for the next time around the loop
            leds[dot] = CRGB::Black;
            delay(30);
        }

  
}


