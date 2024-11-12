FROM openjdk:21-bookworm

RUN apt update
RUN apt install -y build-essential

WORKDIR /work
COPY sumosim/jvm/target/pack .
RUN make install
ENV PATH=/root/local/bin:$PATH

# RUN rm -r /work/*


