# Rest Spring Boot Examples

Collection of working examples of REST-based applications running on top of [Spring Boot](https://projects.spring.io/spring-boot/). We adopted a more in-action example and add a client service. This is useful when you want to bootstrap microservices. We also added a Docker-ready deployment.

**Feel free to submit a PR for other examples or improvements existing ones!**

## Run
Running examples is very easy: just go into one of the examples subfolders, and **run `docker-compose up`**! That's it!


## Examples
* **`greeter`**: client sends a name for the server, and the server replies with a greeting. Client name is passed as an env variable in the `docker-compose.yml`.
    * You can also test this with accessing the url `http://localhost:8080/server/greeting?name=Kenobi`

* **`infinite-pingpong`**: an infinite ping-pong between *two* REST services. This emulates the microservice ecosystem where services are both clients and servers at the same time.

## Requirements

Our Docker images are based on [jfloff/docker-thrike](https://github.com/jfloff/docker-thrike) which already everything we need set up, namely: Tomcat and Gradle (it also has other stuff for gRPC deployments). If you want to bypass Docker and deploy on our own machine, check the [Dockerfile at jfloff/docker-thrike](https://github.com/jfloff/docker-thrike/blob/master/8.5/Dockerfile) for some hints on how to setup your system.


## More infomation
Please refer to the following links for more information:
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)


## License
The code in this repository, unless otherwise noted, is MIT licensed. See the LICENSE file in this repository.