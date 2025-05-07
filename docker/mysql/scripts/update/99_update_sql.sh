#!/bin/bash

########################################################################################
# 하위 update 폴더 내 실행
# update 폴더의 실행했던 파일 마지막 파일명 저장
# 위의 파일이 존재 하지 않으면 순서대로 실행 후 마지막 실행 파일명 저장
# 파일명보다 하위에 있으면 실행하지 않고 상위의 파일들을 실행
# 목록은 날짜 / 알파벳 순으로 처리
#
# 쿼리 수행 기록 남겨놓은 파일 : ${DB_DATA_DIR}/.update_info
# 한번 설치 후 업데이트 방법 : docker-compose exec moneybook-master sync
########################################################################################

set -o errexit
set -o nounset
set -o pipefail

. /opt/bitnami/scripts/libmysql.sh
. /opt/bitnami/scripts/mysql-env.sh

##### 메인 함수
start_bootstrap=$(date +"%s") # start 시간 체크

start=$(date +"%Y-%m-%d %H:%M:%S")
echo "============================================================"
echo "# start : ${start}"
echo "============================================================"

###################################################################
# CURRENT_PATH=$( cd "$(dirname "$0")" ; pwd )
CURRENT_PATH="/docker-entrypoint-initdb.d"

UPDATE_HISTORY="${DB_DATA_DIR}/.update_info"
UPDATED_FILE=""
if [ -f "$UPDATE_HISTORY" ]; then
  UPDATED_FILE=$(cat "${UPDATE_HISTORY}")
fi
echo "LATEST UPDATE DATE : ${UPDATED_FILE}"
UPDATE_FILE=""
wait_for_mysql_access "$DB_ROOT_USER"

# update 폴더 내 파일 존재 여부 확인
if compgen -G "$CURRENT_PATH/update/*" > /dev/null; then
  for f in $CURRENT_PATH/update/*; do
    [[ ! -f "$f" ]] && continue

    UPDATE_FILE="$(basename "${f}")"
    UPDATE_FILE=${UPDATE_FILE%.*}

    echo "${UPDATED_FILE} / ${UPDATE_FILE}"
    [[ -n "$UPDATED_FILE" ]] && [[ $UPDATED_FILE -ge $UPDATE_FILE ]] && continue

    echo "execute : ${UPDATE_FILE}"
    if ! mysql_execute "$DB_DATABASE" "$DB_ROOT_USER" "$DB_ROOT_PASSWORD" < "$f"; then
      error "Failed executing $f"
    fi
  done
else
  echo "No update files found in ${CURRENT_PATH}/update/ - skipping update."
fi

echo "UPDATE HISTORY FILE : ${UPDATE_FILE}"
if [ -n "$UPDATE_FILE" ]; then
  echo "$UPDATE_FILE" > "$UPDATE_HISTORY"
fi

###################################################################
end=$(date +"%Y-%m-%d %H:%M:%S")
echo "============================================================"
echo "# end : ${end}"
echo "============================================================"

end_bootstrap=$(date +"%s") # 종료 시간 체크
difftime=$(( end_bootstrap - start_bootstrap ))
difftime=$(( difftime % 60 ))
echo "============================================================"
echo "# diff : ${start_bootstrap} / ${end_bootstrap} / ${difftime}"
echo "============================================================"
