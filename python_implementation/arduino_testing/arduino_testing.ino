void setup() {
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    byte b = Serial.read();
    Serial.write(b); // echo it back
  }
}