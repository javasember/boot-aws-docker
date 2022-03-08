FROM public.ecr.aws/lambda/java:11

COPY build/classes/java/main /var/task
COPY build/resources/main /var/task
COPY build/dependency/* /var/task/lib/

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest" ]