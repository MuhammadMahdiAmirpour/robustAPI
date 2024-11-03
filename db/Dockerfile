FROM docker.arvancloud.ir/mysql:latest

# Copy the SQL scripts
COPY ./mysql-init /scripts/

# Create a script to run the SQL files
RUN echo '#!/bin/bash\n\
set -e\n\
mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < /scripts/01-create-database.sql\n\
mysql -uroot -p"$MYSQL_ROOT_PASSWORD" < /scripts/02-init.sql\n\
' > /docker-entrypoint-initdb.d/run_scripts.sh

# Make the script executable
RUN chmod +x /docker-entrypoint-initdb.d/run_scripts.sh
