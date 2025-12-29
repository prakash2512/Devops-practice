#!/bin/bash

BACKUP_DIR="/var/backups/docker-logs"
TIMESTAMP=$(date +%F_%H-%M-%S)

CONTAINERS=(
  "med-bck"
  "med-fnt"
  "med-chatbot"
)

mkdir -p "$BACKUP_DIR"

for NAME in "${CONTAINERS[@]}"; do
  ID=$(docker inspect --format='{{.Id}}' "$NAME" 2>/dev/null)

  if [ -z "$ID" ]; then
    echo "⚠️ Container $NAME not found, skipping"
    continue
  fi

  echo "📦 Backing up logs for $NAME ($ID)"

  # 1️⃣ Human-readable logs
  docker logs "$NAME" \
    > "$BACKUP_DIR/${NAME}_${TIMESTAMP}.log"

  # 2️⃣ Raw Docker JSON logs (ID-based)
      cp \
    /var/lib/docker/containers/$ID/${ID}-json.log \
    "$BACKUP_DIR/${NAME}_${TIMESTAMP}_raw.json"

done

echo "✅ Log backup completed"

