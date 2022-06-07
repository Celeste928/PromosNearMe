#include <Servo.h>
#include <Wire.h>
#include <SPI.h>
#include <Adafruit_BLE_Firmata.h>
#include <Adafruit_CircuitPlayground.h>

#include "Adafruit_BLE_Firmata_Boards.h"

#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"

// COMMON SETTINGS
// ----------------------------------------------------------------------------------------------
// These settings are used in both SW UART, HW UART and SPI mode
// ----------------------------------------------------------------------------------------------
#define BUFSIZE                        128   // Size of the read buffer for incoming data
#define VERBOSE_MODE                   true  // If set to 'true' enables debug output


// SOFTWARE UART SETTINGS
// ----------------------------------------------------------------------------------------------
// The following macros declare the pins that will be used for 'SW' serial.
// You should use this option if you are connecting the UART Friend to an UNO
// ----------------------------------------------------------------------------------------------
#define BLUEFRUIT_SWUART_RXD_PIN       9    // Required for software serial!
#define BLUEFRUIT_SWUART_TXD_PIN       10   // Required for software serial!
#define BLUEFRUIT_UART_CTS_PIN         11   // Required for software serial!
#define BLUEFRUIT_UART_RTS_PIN         -1   // Optional, set to -1 if unused


// HARDWARE UART SETTINGS
// ----------------------------------------------------------------------------------------------
// The following macros declare the HW serial port you are using. Uncomment
// this line if you are connecting the BLE to Leonardo/Micro or Flora
// ----------------------------------------------------------------------------------------------
#define BLUEFRUIT_HWSERIAL_NAME      Serial1


// SHARED UART SETTINGS
// ----------------------------------------------------------------------------------------------
// The following sets the optional Mode pin, its recommended but not required
// ----------------------------------------------------------------------------------------------
#define BLUEFRUIT_UART_MODE_PIN        12    // Set to -1 if unused


// SHARED SPI SETTINGS
// ----------------------------------------------------------------------------------------------
// The following macros declare the pins to use for HW and SW SPI communication.
// SCK, MISO and MOSI should be connected to the HW SPI pins on the Uno when
// using HW SPI.  This should be used with nRF51822 based Bluefruit LE modules
// that use SPI (Bluefruit LE SPI Friend).
// ----------------------------------------------------------------------------------------------
#define BLUEFRUIT_SPI_CS               8
#define BLUEFRUIT_SPI_IRQ              7
#define BLUEFRUIT_SPI_RST              4

// SOFTWARE SPI SETTINGS
// ----------------------------------------------------------------------------------------------
// The following macros declare the pins to use for SW SPI communication.
// This should be used with nRF51822 based Bluefruit LE modules that use SPI
// (Bluefruit LE SPI Friend).
// ----------------------------------------------------------------------------------------------
#define BLUEFRUIT_SPI_SCK              13
#define BLUEFRUIT_SPI_MISO             12
#define BLUEFRUIT_SPI_MOSI             11

// Change this to whatever is the Serial console you want, either Serial or SerialUSB
#define FIRMATADEBUG    Serial
// Pause for Serial console before beginning?
#define WAITFORSERIAL   false
// Print all BLE interactions?
#define VERBOSE_MODE    false

Adafruit_BluefruitLE_UART bluefruit(BLUEFRUIT_HWSERIAL_NAME, BLUEFRUIT_UART_MODE_PIN);

Adafruit_BLE_FirmataClass BLE_Firmata = Adafruit_BLE_FirmataClass(bluefruit);


/* The service information */

int32_t serviceId;
int32_t storeCharId;

bool initialized = false;
int32_t success = 0;

// A small helper
void error(const __FlashStringHelper*err) {
  FIRMATADEBUG.println(err);
  while (1);
}

void setup() {
  if (WAITFORSERIAL) {
    while (!FIRMATADEBUG) delay(1);
  }

  FIRMATADEBUG.begin(115200);
  FIRMATADEBUG.println(F("Adafruit Bluefruit LE Firmata test"));
  //for (uint8_t i=0; i<sizeof(boards_analogiopins); i++) {
  //  FIRMATADEBUG.println(boards_analogiopins[i]);
  //}

  /* Initialise the module */
  FIRMATADEBUG.print(F("Initialising the Bluefruit LE module: "));

  if ( !bluefruit.begin(VERBOSE_MODE) )
  {
    error(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
  }

  FIRMATADEBUG.println( F("OK!") );

  /* Perform a factory reset to make sure everything is in a known state */
  FIRMATADEBUG.println(F("Performing a factory reset: "));
  if (! bluefruit.factoryReset() ){
       error(F("Couldn't factory reset"));
  }

  /* Disable command echo from Bluefruit */
  bluefruit.echo(false);

  FIRMATADEBUG.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  bluefruit.info();

  bluefruit.setInterCharWriteDelay(5); // 5 ms

  FIRMATADEBUG.println("Setting name to UW Thermo-Clicker");
  bluefruit.sendCommandCheckOK(F("AT+GAPDEVNAME=Boba on the Ave#PromosNearMe"));

  bluefruit.sendCommandCheckOK( F("AT+GATTCLEAR"));

  /* Add the Service definition */
  /* Service ID should be 1 */
  FIRMATADEBUG.println(F("Adding the Service definition: "));
  bluefruit.sendCommandWithIntReply( F("AT+GATTADDSERVICE=UUID128=bb-7c-54-2c-e0-6e-11-ec-9d-64-02-42-ac-12-00-02"), &serviceId);
  FIRMATADEBUG.println(serviceId);

  /* Add the characteristic for getTemperature */
  /* Chars ID should be 1 */
  FIRMATADEBUG.println(F("Adding the location characteristic: "));
  bluefruit.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=f2-fd-d1-c8-e0-6e-11-ec-9d-64-02-42-ac-12-00-02, PROPERTIES=0x02, MIN_LEN=12, DATATYPE=STRING"), &storeCharId);
  FIRMATADEBUG.println(storeCharId);
//
//  /* Add the get button click notification characteristic */
//  /* Chars ID should be 2 */
//  FIRMATADEBUG.println(F("Adding the categories characteristic: "));
//  bluefruit.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=0f-ae-e1-9a-e0-6f-11-ec-9d-64-02-42-ac-12-00-02, PROPERTIES=0x02, MIN_LEN=20"), &catCharId);
//  FIRMATADEBUG.println(catCharId);
//
//  FIRMATADEBUG.println(F("Adding the promoption characteristic: "));
//  bluefruit.sendCommandWithIntReply( F("AT+GATTADDCHAR=UUID128=17-f7-ff-23-46-80-43-22-ad-f0-af-d4-93-91-0a-8b, PROPERTIES=0x02, MIN_LEN=30"), &promoptionCharId);
//  FIRMATADEBUG.println(promoptionCharId);


//  FIRMATADEBUG.print(F("Adding the Service UUID to the advertising payload: "));
//  bluefruit.sendCommandCheckOK( F("AT+GAPSETADVDATA=02-01-06") );

  FIRMATADEBUG.print(F("Performing a SW reset (service changes require a reset): "));
  bluefruit.reset();

  FIRMATADEBUG.println();

  CircuitPlayground.begin();
  initialized = true;
}

void loop() {
  while (!initialized)
  {
    return;
  }


  bluefruit.print( F("AT+GATTCHAR=") );
  bluefruit.print( storeCharId );
  bluefruit.print( F(",") );
  bluefruit.println("000000000001");


  /* Check if command executed OK */
//  if ( !bluefruit.waitForOK() )
//  {
//    FIRMATADEBUG.println(F("Failed to get response!"));
//  }


  /* Check if command executed OK */
//  if ( !bluefruit.waitForOK() )
//  {
//    FIRMATADEBUG.println(F("Failed to get response!"));
//  }

  /* Delay before next measurement update */
  FIRMATADEBUG.println("Done!");
  delay(1000);
}
