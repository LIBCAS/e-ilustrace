FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.4

RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch analysis-icu

# Copy config files 
COPY config/hunspell /usr/share/elasticsearch/config/hunspell

