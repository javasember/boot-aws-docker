# boot-aws-docker
Prototype for containerized Spring Boot app used for AWS Lambda

## Gradle setup

I created the gradle build file in Spring Initialzr, so you should not mess with finding versions of Spring Cloud. The containerized app does not need the BootJar packaging,
neither the thin layout/shaded fckery which is a heavily experimental tool with marginal support. The only major modification here is adding the dependency copy
to the build folder, so you'll have all the classes, resources and dependencies in one place.
```
task copyRuntimeDependencies(type: Copy) {
    from configurations.runtimeClasspath
    into 'build/dependency'
}

build.dependsOn copyRuntimeDependencies
```

Building the app for the lambda container
```
gradle clean build
```

## The application

Tis but a simple app with `Function`, `Supplier` or `Consumer` implementations which are needed to be annotated with `@Component` for scanning purposes. When you have
multiple callable endpoints, you have to specify which method you would like to use in the Lambda in the `application.yaml`. To make it configurable externally, I set it
to use an environment variable
```
spring.cloud.function.definition: ${FUNCTION_NAME}
```

## Docker image

AWS Lambda can be created by using containers. The base image must be an AWS Lambda image, that is here a Java 11 specific one. You need to copy all the classes, resources
and dependencies to the `/var/task` dir, and add the Spring Boot request handler in CMD that initializes the application and calls the proper methods. Most of the time
the handler won't be able to find the main class of the application, neither the method you'd like to map your request to, so you'll need to specify it in environment variables.
Locally you can use the `env.txt` file, in AWS config add the variables in the Lambda config section.
```
MAIN_CLASS=com.gys.lambda.LambdaApplication
FUNCTION_NAME=queryFunction
```

Building the image
```
docker build -t <image name> .
```

Running the image locally
```
docker run -p 9000:8080 --env-file env.txt <image name>
```

Testing the functionality locally
```
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '"Dick Chadson"'
```

## Pushing the image to ECR

First of all, create an ECR repository. Since AWS access is secured with MFA most of the time, you need to enable key based access to the repository in the IAM console.
Also make sure you initialzed the environment locally with AWS CLI. When it's done the only tool you need
[Docker ECR credential helper](https://github.com/awslabs/amazon-ecr-credential-helper) to make docker access your repo.

For tagging the image for ECR push you need to use this format when building the image
```
docker build -t <account_id>.dkr.ecr.<region_name>.amazonaws.com/<repo_name>:<tag>
```

Pushing the image to ECR
```
docker push <account_id>.dkr.ecr.<region_name>.amazonaws.com/<repo_name>:<tag>
```

## Creating the Lambda function

You only need to create a new one with the image URI. Set the environment variables, also add memory as you probably need much more than the default 128MB.
Also create a gateway trigger with some HTTP API, and you can test it with sending a name in JSON string in the request body by using the console.
Have fun :)
