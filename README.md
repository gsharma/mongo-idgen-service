# Mongo Id Generator Service

## Overview
Mongo Id Generator Service provides its consumers with fast, globally-unique and sparse id's.  

## Using the Service
The API is super simple and there's a single method that callers can invoke: generateId()  
The service can be scaled out as needed simply by speeding up id cache rejuvenation frequency and beefing up the id cache size. If even that is not sufficient, the backing datastore can be beefed up (shardng + replicasets) but I believe that won't be necessary.  

Also provided is a choice of underlying implementations: a MongoDB backed and another In-Memory service.  
