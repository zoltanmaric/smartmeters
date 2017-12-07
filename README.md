# SmartMeters

A naive implementation of a real-time reporting system for smart
meter readings.

## Problem Statement

A system of clients (smart meters) which report the net
consumption of energy of a consumer in real-time (e.g. every
5 s) to a server.

### Server
* Collects periodical net meter readings via a JSON REST API
* Each reading consists of
  * The net status of the smart meter (Wh in, Wh out)
  * The timestamp of the reading
  * The public key of the meter
  * The cryptographic signature over the status and the timestamp
* When a reading is received, the signature is verified and the
status and timestamp are recorded in the database

### Clients
* Clients are registered via the `/register` endpoint. Access to
this endpoint is assumed to be limited to operators within a
restricted private network
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
* Using the private key, which is considered to be securely stored
in the hardware of the smart meter and unique to each meter, the
reading data is signed

## Implementation
#### Server
* The server offers 2 endpoints:
  * `POST /register` (restricted to private network)
  * `POST /reportReading` (public)
* The users and readings are stored in a local PostgreSQL database
* Each reading contains a signature over the reading and a public
key
  * Messages signed with a public key which doesn't belong to a
  registered user are discarded
  * Adversaries will not be able to submit readings with a
  registered user's public key because they will not be able to
  sign the message without the user's private key
  
#### Clients Simulator
* A console application for simulating a swarm of smart meters
* When run, it performs 2 stages:
  1. Registers a configurable number of users (100 by default)
     * Generates an RSA key pair
     * Submits the public key with a randomly generated username to
     the `/register` endpoint
     * Keeps the private key and username in memory
  2. Periodically reports random readings (at a configurable
  interval, 5 s by default) for each smart meter
     * The start of reporting for each meter is randomized, to
     prevent "waves" of reports
     * Each reading and timestamp is signed with the stored private
     key of the smart meter
     * The total number of readings per meter is configurable
     (6 by default, thus making the readings phase duration ~30s)
    
    
## Installation
#### Requirements
* JDK (implemented with version 8)
* [SBT](http://www.scala-sbt.org/download.html) (implemented with version 1.0.4)
* [PostgreSQL](https://www.postgresql.org/download/) (implemented with version 9.6.6)

#### Installation Steps
##### Configuring PostgreSQL
1. Create a PostgreSQL user `sonnen`
2. Create a database `sonnen` and configure a password for it


1. Clone the repository
```bash
git clone git@github.com:zoltanmaric/smartmeters.git
```
2. Create a file called `secret.conf` in
`server/conf/secret.conf`
3. Enter the DB password for `sonnen` in `secret.conf`
```hocon
db.default.password = <password>
```
4. Run SBT
```bash
cd smartmeters
sbt
```
5. Run the server from within the SBT console
   * This step will take a while, as it downloads all JAR
   dependencies of the project
```sbtshell
project server
run
```
6. When the server has started (look for the yellow/green 
`(Server started, use Enter to stop and go back to the console...)`
message in the console), open http://localhost:9000 in a browser
   * On first run, there will be an error page saying that
   evolutions need to be applied. Press the `Apply` button. This
   will create the necessary database tables
   * You should now see the standard Play Framework welcome page
7. Start another SBT session in another terminal
```bash
sbt
```
8. Start the clients simulator
   * You may want to look at or edit the configuration parameters
   in `clients/src/main/resources/reference.conf`
```sbtshell
project clients
run
```
