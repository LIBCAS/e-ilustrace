FROM httpd:alpine

# Apache conf
COPY ./docker/httpd.conf /usr/local/apache2/conf/httpd.conf
COPY ./docker/.htaccess /usr/local/apache2/htdocs/
