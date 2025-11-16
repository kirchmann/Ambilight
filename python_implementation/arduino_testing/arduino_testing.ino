void setup() {
  Serial.begin(115200);
}

void loop() {
  if (Serial.available()) {
    byte b = Serial.read();
    Serial.write(b); // echo it back
  }
}