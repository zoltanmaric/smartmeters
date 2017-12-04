# Problem Statement

## Server
* Collects periodical net meter readings via a JSON REST API
* Each reading consists of
  * The net status of the smart meter (Wh in, Wh out)
  * The timestamp of the reading
  * The public key of the meter
  * The cryptographic signature over the status and the timestamp
* When a reading is received, the signature is verified and the
status and timestamp are recorded in the database

## Clients
* ~~Using the server's public key, the reading data is
asymmetrically encrypted~~
  * HTTPS makes sure that the data is encrypted in transit
  * HTTPS to be implemented later
* Using a private key, which is considered to be securely stored
in the hardware of the smart meter and unique to each meter, the
reading data is signed
* Clients register via the `/register` endpoint, which is a
simplification for the purposes of this proof-of-concept
  * In reality, the process of registration would consist of
  purchasing a subscription e.g. via a credit card and eventually
  having a smart meter installed containing the private key
  * This implementation short-circuits this process: an operator
  registers a user by `POST`ing their username and the public key
  of their smart meter
  * After registering, the public key of the generated key pair
  for this user is stored with the username on the server,
  thus constituting a whitelist of addresses that can report
  readings


## Ideas
* Registration via Ether & Blockchain?
* Reporting storage level instead?
  * Or in addition to the smart meter reading?