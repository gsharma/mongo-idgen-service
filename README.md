# Mongo Id Generator Service

## Overview
Mongo Id Generator Service provides its consumers with fast, globally-unique and sparse id's.  

## Using the Service
The API is super simple and there's a single method that callers can invoke: generateId()  
The service can be scaled out as needed simply by speeding up id cache rejuvenation frequency and beefing up the id cache size. If even that is not sufficient, the backing datastore can be beefed up (shardng + replicasets) but I believe that won't be necessary.  

The Id root prefix is set to '11610' by default but is configurable via the id.root.prefix property in core.xml.  

Also provided is a choice of underlying implementations: a MongoDB backed and another In-Memory service.  

## Contributions
Fork, spoon, knive the project as you see fit (: Pull requests with bug fixes are very welcome. If you encounter an issue and do not have the time to submit a patch, please log a [Github Issue](https://github.com/gsharma/mongo-idgen-service/issues) against the project.

## License
MIT License - Copyright (c) 2012 Gaurav Sharma
