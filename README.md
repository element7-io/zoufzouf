
CI        | Slack | Status | 
----------|-------|--------|
Codeship  | App         | [![Build Status](https://codeship.com/projects/c82b46a0-e47d-0133-367d-626500d789c1/status?branch=master)](https://codeship.com/projects/146357)
Drone.io  | Next Major  | [![Build Status](https://drone.io/github.com/glnds/zoufzouf/status.png)](https://drone.io/github.com/glnds/zoufzouf/latest)
Travis CI | travis.yml  | [![Build Status](https://travis-ci.org/glnds/zoufzouf.svg?branch=master)](https://travis-ci.org/glnds/zoufzouf)
Snap CI   | Web APi     | [![Build Status](https://snap-ci.com/glnds/zoufzouf/branch/master/build_image)](https://snap-ci.com/glnds/zoufzouf/branch/master)
Circle CI | App         | [![Circle CI](https://circleci.com/gh/glnds/zoufzouf.svg?style=svg)](https://circleci.com/gh/glnds/zoufzouf)
Buddy     | App         | [Not Available](https://app.buddy.works/leendersgert)
Wercker   | wercker.yml | [![wercker status](https://app.wercker.com/status/23fc34d88dcf586ed6032c6951735af7/m "wercker status")](https://app.wercker.com/project/bykey/23fc34d88dcf586ed6032c6951735af7)


*Note: this project is also used to evaluate the different CI/CD solutions listed above.*

# ZoufZouf

Log Analysis for Amazon CloudFront

## Technology stack
- Gradle
- Spock
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
