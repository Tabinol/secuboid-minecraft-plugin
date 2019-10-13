#!/bin/sh
# Bash not found in the cschlosser/alpine-lftps image

lftp ftps://${FTP_TABINOL_ME_USER}:${FTP_TABINOL_ME_PASSWD}@ftp.tabinol.me -e "cd /javadoc; rm -r ${BITBUCKET_REPO_SLUG}; mkdir -p ${BITBUCKET_REPO_SLUG}; cd ${BITBUCKET_REPO_SLUG}; mput target/site/apidocs/*; bye"