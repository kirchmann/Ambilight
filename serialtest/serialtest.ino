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
const int ledPin = 13;       // the pin that the LED is attached to
String inputString = "";         // a string to hold incoming data
boolean stringComplete = false;  // whether the string is complete

void setup() {
  // initialize serial:
  Serial.begin(9600);
  Serial.setTimeout(50);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
    // initialize the LED as an output:
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin,LOW);
}

void loop() {
  // print the string when a newline arrives:
  String text = Serial.readString();
  digitalWrite(ledPin,LOW);
  delay(1000);
  if (text.length()>0) {

    digitalWrite(ledPin,HIGH);
    delay(2000);
  }  
}


