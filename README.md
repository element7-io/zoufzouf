CI Tool   | Badge
----------| -----
Codeship  | [![Build Status](https://codeship.com/projects/c82b46a0-e47d-0133-367d-626500d789c1/status?branch=master)](https://codeship.com/projects/146357)
Drone.io  |  [![Build Status](https://drone.io/github.com/glnds/zoufzouf/status.png)](https://drone.io/github.com/glnds/zoufzouf/latest)
Travis CI | [![Build Status](https://travis-ci.org/glnds/zoufzouf.svg?branch=master)](https://travis-ci.org/glnds/zoufzouf)
Snap CI   | [![Build Status](https://snap-ci.com/glnds/zoufzouf/branch/master/build_image)](https://snap-ci.com/glnds/zoufzouf/branch/master)
Circle CI | [![Circle CI](https://circleci.com/gh/glnds/zoufzouf.svg?style=svg)](https://circleci.com/gh/glnds/zoufzouf)
Wercker   | [![wercker status](https://app.wercker.com/status/23fc34d88dcf586ed6032c6951735af7/m "wercker status")](https://app.wercker.com/project/bykey/23fc34d88dcf586ed6032c6951735af7)


*Note: this project is also used to evaluate the different CI/CD solutions listed above.*

# ZoufZouf

[![Dependency Status](https://www.versioneye.com/user/projects/57206983fcd19a00454423e7/badge.svg?style=flat)](https://www.versioneye.com/user/projects/57206983fcd19a00454423e7)
[![GitHub tag](https://img.shields.io/github/tag/glnds/zoufzouf.svg?style=flat-square)](https://github.com/glnds/zoufzouf/releases)
[![GitHub release](https://img.shields.io/github/release/glnds/zoufzouf.svg?style=flat-square)](https://github.com/glnds/zoufzouf/releases)
[![GitHub license](https://img.shields.io/github/license/glnds/zoufzouf.svg?style=flat-square)](https://github.com/glnds/zoufzouf/blob/master/LICENSE)

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

### Docker

Docker image: https://hub.docker.com/r/glnds/zoufzouf/

- Run with docker-compose: 
	```
	$ docker-compose up
	```

- Run MongoDB as a separate container
	```
	docker run --name my-local-mongo -v mongo-data:/data/db -p 27017:27017 -d mongo
	```
	
### Standalone

To run as standalone application run:
```
./gradlew run
```

*When running the app as standalone either a local MongoDB or MongoDB container should be running.*

[![Analytics](https://ga-beacon.appspot.com/UA-77034813-1/zoufzouf/readme)](https://github.com/igrigorik/ga-beacon)
