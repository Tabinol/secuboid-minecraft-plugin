#!/bin/sh
# Bash not found in the cschlosser/alpine-lftps image

lftp -u ${FTP_TABINOL_ME_USER},${FTP_TABINOL_ME_PASSWD} ftp.tabinol.me -e "set ftp:ssl-allow no; mkdir -p ${BITBUCKET_REPO_SLUG}; mirror  -e -R target/site/apidocs/ javadoc/${BITBUCKET_REPO_SLUG}/; bye"