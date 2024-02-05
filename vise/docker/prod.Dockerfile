# syntax=docker/dockerfile:1
FROM ubuntu:20.04

ENV WEB_NAMESPACE=

# install dependencies
RUN apt update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends tzdata
RUN apt install -y wget git libssl-dev build-essential libpng-dev zlib1g-dev libjpeg-dev unzip libmagick++-dev

# clone vise code
RUN mkdir -p /root/vise/code && mkdir -p /root/vise/dep && mkdir "/root/vise/dep/_tmp_libsrc" && cd /root/vise/code && git clone https://gitlab.com/vgg/vise

# cmake
RUN cd /root/vise/dep/_tmp_libsrc && wget https://github.com/Kitware/CMake/releases/download/v3.20.5/cmake-3.20.5.tar.gz && tar -zxvf cmake-3.20.5.tar.gz && cd cmake-3.20.5 && ./configure --prefix=/root/vise/dep && make -j 2 && make install

# boost
RUN cd /root/vise/dep/_tmp_libsrc && wget https://boostorg.jfrog.io/artifactory/main/release/1.73.0/source/boost_1_73_0.tar.gz && tar -zxvf boost_1_73_0.tar.gz && cd boost_1_73_0 && ./bootstrap.sh --prefix=/root/vise/dep --with-toolset=gcc --with-libraries=filesystem,system,thread && ./b2 --with-filesystem --with-system --with-thread variant=release threading=multi toolset=gcc install

# imagemagick
# RUN cd /root/vise/dep/_tmp_libsrc && wget -O ImageMagick6-6.9.12-16.tar.gz https://github.com/ImageMagick/ImageMagick6/archive/6.9.12-16.tar.gz && tar -zxvf ImageMagick6-6.9.12-16.tar.gz && cd ImageMagick6-6.9.12-16 && ./configure --prefix=/root/vise/dep -enable-hdri=no --with-quantum-depth=8 --disable-dependency-tracking --with-x=no --without-perl && make -j 2 && make install

# google protobuf (https://github.com/protocolbuffers/protobuf/releases/tag/v2.6.1)
RUN cd /root/vise/dep/_tmp_libsrc && wget https://github.com/protocolbuffers/protobuf/releases/download/v2.6.1/protobuf-2.6.1.tar.gz && tar -zxvf protobuf-2.6.1.tar.gz --no-same-owner && cd protobuf-2.6.1 && ./configure --prefix=/root/vise/dep && make -j 2 && make install

# eigen
RUN cd /root/vise/dep/_tmp_libsrc && wget -O eigen-3.3.7.tar.gz https://gitlab.com/libeigen/eigen/-/archive/3.3.7/eigen-3.3.7.tar.gz && tar -zxvf eigen-3.3.7.tar.gz && cd eigen-3.3.7/ && mkdir cmake_build && cd cmake_build && /root/vise/dep"/bin/cmake" -DCMAKE_INSTALL_PREFIX=/root/vise/dep ../ && make -j 2 && make install

# vlfeat
RUN cd /root/vise/dep/_tmp_libsrc && wget --no-check-certificate http://www.vlfeat.org/download/vlfeat-0.9.21-bin.tar.gz && tar -zxvf vlfeat-0.9.21-bin.tar.gz && cd vlfeat-0.9.21 && sed -i 's/default(none)//g' vl/kmeans.c && make -j 2 && cp "/root/vise/dep/_tmp_libsrc/vlfeat-0.9.21/bin/glnxa64/libvl.so" "/root/vise/dep/lib/libvl.so" && mkdir "/root/vise/dep/include/vl" && cp -fr /root/vise/dep/_tmp_libsrc/vlfeat-0.9.21/vl/*.* "/root/vise/dep/include/vl/"

# sqlite
RUN cd /root/vise/dep/_tmp_libsrc && wget https://www.sqlite.org/2020/sqlite-autoconf-3330000.tar.gz && tar -zxvf sqlite-autoconf-3330000.tar.gz && cd sqlite-autoconf-3330000 && ./configure --prefix=/root/vise/dep && make -j 2 && make install

# compile VISE
RUN cd /root/vise/code/vise/ && mkdir cmake_build && cd cmake_build && /root/vise/dep/bin/cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_PREFIX_PATH=/root/vise/dep -DVLFEAT_LIB=/root/vise/dep/_tmp_libsrc/vlfeat-0.9.21/bin/glnxa64/libvl.so -DVLFEAT_INCLUDE_DIR=/root/vise/dep/_tmp_libsrc/vlfeat-0.9.21/ ../src && make -j 2

# remove temp files
RUN rm -rf /root/vise/dep/_tmp_libsrc/ /root/vise/code/vise/cmake_build/search_engine /var/lib/apt/lists/*

# download generic visual vocabulary to VISE application folder
RUN mkdir -p /root/.vise/asset/relja_retrival/visual_vocabulary/ && cd /root/.vise/asset/relja_retrival/visual_vocabulary/ && wget https://www.robots.ox.ac.uk/~vgg/software/vise/download/2.x.y/relja_retrival/generic-visual-vocabulary/imcount53629-imsize400x400-voc10k-hamm32.zip && unzip imcount53629-imsize400x400-voc10k-hamm32.zip && mv imcount53629-imsize400x400-voc10k-hamm32 latest && rm imcount53629-imsize400x400-voc10k-hamm32.zip

# create a symbolic link for VISE web application
RUN cd /root/.vise && mkdir project && ln -s /root/vise/code/vise/src/www www &&  mkdir -p /root/.vise/asset/relja_retrival/visual_vocabulary/latest

# add running time
ADD docker/run.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/run.sh

EXPOSE 9669
CMD ["run.sh"]
