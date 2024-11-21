##Backup Script
#!/bin/bash
source_dir="/path/to/source"
backup_dir="/path/to/backup"
date=$(date +%Y-%m-%d)

mkdir -p $backup_dir/$date
cp -r $source_dir/* $backup_dir/$date/

echo "Backup completed successfully!"

## DISK memory reachs high
#!/bin/bash
threshold=80
usage=$(df / | grep / | awk '{ print $5 }' | sed 's/%//g')

if [ $usage -gt $threshold ]; then
  echo "Warning: Disk usage is above $threshold%!"
else
  echo "Disk usage is below $threshold%."
fi
