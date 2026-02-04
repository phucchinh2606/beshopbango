FROM ubuntu:latest
LABEL authors="phucchinh"

ENTRYPOINT ["top", "-b"]