
CI        |  Status
----------|-------
Snap CI   | [![Build Status](https://snap-ci.com/glnds/zoufzouf/branch/master/build_image)](https://snap-ci.com/glnds/zoufzouf/branch/master)
Travis CI | [![Build Status](https://travis-ci.org/glnds/zoufzouf.svg?branch=master)](https://travis-ci.org/glnds/zoufzouf)


# ZoufZouf

Log Analysis for Amazon CloudFront


## Technology stack
- Java 8
- Persistence layer options
	- MongoDB

## Usage

### Configuration

Put a ```properties.yml``` file under your resources root folder.

Here's an example:
```
# AWS Access
awsAccessKey: A....
awsSecretKey: xyz..

# Run mode
dryRun: true

# Persistance Store
storage: MongoDB
servers:
  - 192.168.99.100
```

## Run

### Run using MongoDB

Run MongoDb as a docker container

	docker run --name my-local-mongo -v mongo-data:/data/db -p 27017:27017 -d mongo
