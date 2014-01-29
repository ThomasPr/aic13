# AIC 2013 - Group 4 - Cloudcomputing

The project contains the follwing assets:
* Code for AWS (Step 1) in `code/cloudcomputing`
* Code for GAE (Step 2) in `code/cloudcomputing-gae`
* Paper in `docs/Tex/aic.pdf`


## AWS (Step 1)

### Procedure to use the preconfigured AWS instance:
* Login to the AWS Management Console and switch to the Asia Pacific (Tokyo) region
* Start the instances DB and WEB/MQ
* Login via `ssh ubuntu@54.238.204.152` and use the provided password
* `cd aic/code/cloudcomputing/`
* Run `mvn clean package exec:exec`
* Point your browser to http://54.238.204.152:8080 and use an arbritary username to login
* Access cloud statistics at http://54.238.204.152:8080/stats

### More general instructions:
* Setup a Postgresql database and import the file `schema.sql`
* Configure database settings in `src/main/resources/database.properties`
* Optional: Import tweets into the database (see below)
* Configure AWS API Keys in `src/main/resources/ec2.properties`
* Run `mvn clean package exec:exec`

#### Import Tweets into the database (optional):
* Download tweets.txt.bz from TUWEL
* Extract this file to get tweets.txt
* Adjust the constant `TWEETS_FILE` at `at.ac.tuwien.aic.group4.cloudcomputing.importer.Importer`
* Run `mvn -PimportTweets clean package exec:exec` (takes ages)

To speedup the import of the tweets into the database you can drop the both indexes as defined in schema.sql. Make sure to add them again after the Import has finished.


## GAE (Step 2)

* Optional: Import tweets into the database (see below)
* Modify `application` in `src/main/webapp/WEB-INF/appengine-web.xml`
* Run `mvn appengine:backends_update appengine:update` to deploy to GAE 

### Import Tweets (optional):
* Download tweets.txt.bz from TUWEL
* Extract this file to get tweets.txt
* Adjust the constant `TWEETS_FILE` at `at.ac.tuwien.aic.group4.cloudcomputing.importer.Importer`
* Run `mvn -PimportTweets clean package exec:exec` (takes ages)
