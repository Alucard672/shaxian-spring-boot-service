#!/bin/bash

rsync -av --progress --exclude-from=.gitignore ./ root@120.27.148.45:/data/codes/shaxian-spring-boot-service/