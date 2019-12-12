FROM postgres:9.6
MAINTAINER Pavel Alexeev

# Do NOT use /var/lib/postgresql/data/ because its declared as volume in base image and can't be undeclared but we want persist data in image
ENV PGDATA /var/lib/pgsql/data/
ENV pgsql 'psql -U postgres -nxq -v ON_ERROR_STOP=on --dbname somedb'
ENV DB_DUMP_URL 'ftp://user:password@ftp.somehost.com/desired_db_backup/somedb_dump-2017-02-21-16_55_01.sql.gz'

COPY docker-entrypoint-initdb.d/* /docker-entrypoint-initdb.d/
COPY init.sql/* /init.sql/

# Later in RUN we hack config to include conf.d parts.
COPY postgres.conf.d/* /etc/postgres/conf.d/

# Unfortunately Debian /bin/sh is dash shell instead of bash (https://wiki.ubuntu.com/DashAsBinSh) and some handy options like pipefaile is unavailable
# Separate RUN to next will be in bash instead of dash. Change /bin/sh symlink as it is hardcoded https://github.com/docker/docker/issues/8100
RUN ln -sb /bin/bash /bin/sh

RUN set -euo pipefail \
    && echo '1) Install required packages' `# https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/#apt-get` \
        && apt-get update \
        && apt-get install -y \
            curl \
            postgresql-plperl-9.6 \
    && echo '3) Run postgres DB internally for init cluster:' `# Example how to run instance of service: http://stackoverflow.com/questions/25920029/setting-up-mysql-and-importing-dump-within-dockerfile`\
        && bash -c '/docker-entrypoint.sh postgres --autovacuum=off &' \
            && sleep 10 \
    && echo '4.1) Configure postgres: use conf.d directory:' \
        && sed -i "s@#include_dir = 'conf.d'@include_dir = '/etc/postgres/conf.d/'@" "$PGDATA/postgresql.conf" \
    && echo '4.2) Configure postgres: Do NOT chown and chmod each time on start PGDATA directory (speedup on start especially on Windows):' \
        && sed -i 's@chmod 700 "$PGDATA"@#chmod 700 "$PGDATA"@g;s@chown -R postgres "$PGDATA"@#chown -R postgres "$PGDATA"@g' /docker-entrypoint.sh \
    && echo '4.3) RERun postgres DB for work in new configuration:'\
        && gosu postgres pg_ctl -D "$PGDATA" -m fast -w stop \
            && sleep 10 \
        && bash -c '/docker-entrypoint.sh postgres --autovacuum=off --max_wal_size=3GB &' \
            && sleep 10 \
    && echo '5) Populate DB data: Restore DB backup:' \
        && time curl "$DB_DUMP_URL" \
            | gzip --decompress \
                | grep -Pv '^((DROP|CREATE|ALTER) DATABASE|\\connect)' \
                    | $pgsql \
    && echo '6) Execute build-time sql scripts:' \
        && for f in /init.sql/*; do echo "Process [$f]"; $pgsql -f "$f"; rm -f "$f"; done \
    && echo '7) Update DB to current migrations state:' \
        && time java -jar target/db-updater-*.jar -f flyway.url=jdbc:postgresql://localhost:5432/somedb -f flyway.user=postgres -f flyway.password=postgres \
    && echo '8) Vacuum full and analyze (no reindex need then):' \
        && time vacuumdb -U postgres --full --all --analyze --freeze \
    && echo '9) Stop postgres:' \
    && gosu postgres pg_ctl -D "$PGDATA" -m fast -w stop \
        && sleep 10 \
    && echo '10) Cleanup pg_xlog required to do not include it in image!:' `# Command inspired by http://www.hivelogik.com/blog/?p=513` \
        && gosu postgres pg_resetxlog -o $( LANG=C pg_controldata $PGDATA | grep -oP '(?<=NextOID:\s{10})\d+' ) -x $( LANG=C pg_controldata $PGDATA | grep -oP '(?<=NextXID:\s{10}0[/:])\d+' ) -f $PGDATA \
    && echo '11(pair to 1)) Apt clean:' \
        && apt-get autoremove -y \
            curl \
        && rm -rf /var/lib/apt/lists/*