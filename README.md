#Recommendation Service API

### <h3>Description
Short Akka implementation of a recommendation engine, full typelevel stack version coming soon

### <h3>Installation instructions and running:<h3>
1. Download and Install the following:
   * Scala 2.13.13
   * SBT Version 1.3.13
   * Postgresql 15.4
2. run `chmod +xw ./scripts/install_db.sh`
3. run `./scripts/install_db.sh`
4. to run tests use `sbt -Dconfig.file=src/test/resources/application-test.conf test;`
5. If you want to use the DB please insert data into `movies and tags tables`
6. run `sbt run` in your terminal at the root of this project 
7. use the api contract in `docs/openapi.yml` to aid you in calling this api

### <h3>Design decisions:<h3>
* Used Akka http to serve a restful API
* Cats allows us to easily compose functions together (with monads) and take advantage of category theory.
* Dependency injection allows for easier component construction and injecting dependencies for classes when required.
* Refined types could be used for improved type level validation but I did not have time for this
* Uses Postgres DB for optional storage of movies
* Score by tags runs into a problem where if you match a tag exactly e.g "Crime" = "Crime" it is given a score of 2 and when it doesn't match exactly e.g "Crime" = "Crime Family" it gives a score of 1.
  So when you get e.g "Crime Family" != "Crime" you get 0. I think this is fine because as a business has put "Crime" and "Crime Family" in the list of tags for one film. Meaning "Crime Family" comes under the umbrella of "Crime" but not the other way around, I think
  this is reasonable reasoning to why this algorithm remains accurate within business means.
* Also implemented a check on similar movie lengths
* From the given data points are don't think there is more refinement that can be done. Matching on title would not be a good Idea for a recommendation engine. That is like saying Aquaman is similar to Alvin and the chipmunks because they both have the Char 'A'