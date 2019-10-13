#!/bin/bash

sed -i~ "/<servers>/ a\
<server>\
  <id>tabinol-maven-repo</id>\
  <username>${FTP_TABINOL_ME_USER}</username>\
  <password>${FTP_TABINOL_ME_PASSWD}</password>\
</server>" /usr/share/maven/conf/settings.xml