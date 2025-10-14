#!/bin/bash

read -p "Enter migration name: " migration_name

datetime=$(date '+%Y%m%d%H%M%S%m')

filename="V${datetime}__${migration_name}.sql"

directory="src/main/resources/db/migration"

touch "${directory}/${filename}"