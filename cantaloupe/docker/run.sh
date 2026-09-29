#!/bin/sh
set -e

# change workdir
cd /srv/cantaloupe-5.0.5

# change filepath to images
sed -i "s|FilesystemSource.BasicLookupStrategy.path_prefix = /home/myself/images/| FilesystemSource.BasicLookupStrategy.path_prefix = ${image_source}|g" cantaloupe.properties.sample  

# start cantaloupe
java -Dcantaloupe.config=cantaloupe.properties.sample -jar cantaloupe-5.0.5.jar
