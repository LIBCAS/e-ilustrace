# syntax=docker/dockerfile:1
FROM openjdk:11-jre

ENV image_source=/tmp/images

# install dependencies
RUN cd srv && \
    wget https://github.com/cantaloupe-project/cantaloupe/releases/download/v5.0.5/cantaloupe-5.0.5.zip && \
    unzip cantaloupe-5.0.5.zip

# add running time
ADD docker/run.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/run.sh

EXPOSE 8182
CMD ["run.sh"] 
